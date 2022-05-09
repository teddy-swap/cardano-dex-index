package fi.spectrumlabs.programs

import cats.syntax.foldable._
import cats.{Foldable, FunctorFilter, Monad}
import fi.spectrumlabs.config.TrackerConfig
import fi.spectrumlabs.models.Transaction
import fi.spectrumlabs.repositories.TrackerCache
import fi.spectrumlabs.services.{Explorer, Filter}
import fi.spectrumlabs.streaming.{Producer, Record}
import mouse.any._
import tofu.streams.{Compile, Evals, Temporal}
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
    C[_]: Foldable
  ](producer: Producer[String, Transaction, S], config: TrackerConfig)(
    implicit cache: TrackerCache[F],
    explorer: Explorer[S, F],
    filter: Filter[F]
  ): TrackerProgram[S] = new Impl[S, F, C](producer, config)

  private final class Impl[S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F], F[
    _
  ]: Monad, C[_]: Foldable](producer: Producer[String, Transaction, S], config: TrackerConfig)(
    implicit cache: TrackerCache[F],
    explorer: Explorer[S, F],
    filter: Filter[F]
  ) extends TrackerProgram[S] {

    def run: S[Unit] =
      eval(cache.getLastOffset) >>= exec

    def exec(offset: Int): S[Unit] = {
      println(s"Current offset is: $offset")
      explorer
        .streamTransactions(offset, config.limit)
        .groupWithin(config.batchSize, config.timeout)
        .evalMap { batch =>
          println(s"Got batch: ${batch.size}")
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
}
