package fi.spectrumlabs.programs

import cats.syntax.foldable._
import cats.{Foldable, Functor, FunctorFilter, Monad}
import fi.spectrumlabs.config.TrackerConfig
import fi.spectrumlabs.core.models.Transaction
import fi.spectrumlabs.repositories.TrackerCache
import fi.spectrumlabs.services.{Explorer, Filter}
import fi.spectrumlabs.streaming.{Producer, Record}
import mouse.any._
import tofu.logging.{Logging, Logs}
import tofu.streams.{Compile, Evals, Temporal}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.compile._
import tofu.syntax.streams.emits._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._

trait TrackerProgram[S[_]] {
  def run: S[Unit]
}

object TrackerProgram {

  def create[
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F],
    F[_]: Monad,
    C[_]: Foldable,
    I[_]: Functor
  ](producer: Producer[String, Transaction, S], config: TrackerConfig)(
    implicit cache: TrackerCache[F],
    explorer: Explorer[S, F],
    filter: Filter[F],
    logs: Logs[I, F]
  ): I[TrackerProgram[S]] =
    logs.forService[TrackerProgram[S]].map(implicit __ => new Impl[S, F, C](producer, config))

  private final class Impl[S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F], F[
    _
  ]: Monad: Logging, C[_]: Foldable](producer: Producer[String, Transaction, S], config: TrackerConfig)(
    implicit cache: TrackerCache[F],
    explorer: Explorer[S, F],
    filter: Filter[F]
  ) extends TrackerProgram[S] {

    def run: S[Unit] =
      eval(cache.getLastOffset) >>= exec

    def exec(offset: Int): S[Unit] =
      eval(info"Current offset is: $offset") >>
      explorer
        .streamTransactions(offset, config.limit)
        .groupWithin(config.batchSize, config.timeout)
        .evalMap { batch =>
          info"Got batch of txn of size ${batch.size}. Last txn id is: ${batch.toList.lastOption.map(_.hash)}." >>
          filter
            .filter(batch.toList)
            .map(_.map { txn =>
              Record(txn.hash.value, txn)
            })
        }
        .evalMap { txn =>
          (emits[S](txn) |> producer.produce).drain
        }
        .evalMap { _ =>
          val newOffset = offset + config.limit
          cache.setLastOffset(newOffset).as(newOffset)
        } >>= exec
  }
}
