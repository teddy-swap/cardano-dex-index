package fi.spectrumlabs.programs

import cats.{Foldable, FunctorFilter, Monad}
import fi.spectrumlabs.models.{Offset, Transaction}
import fi.spectrumlabs.repositories.TrackerCache
import fi.spectrumlabs.services.Explorer
import fi.spectrumlabs.services.Filter
import fi.spectrumlabs.streaming.Producer
import tofu.streams.{Compile, Evals, Temporal}
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._
import tofu.syntax.streams.filter._
import cats.syntax.foldable._
import cats.syntax.traverse._
import tofu.syntax.streams.emits._
import tofu.syntax.streams.compile._
import mouse.any._

import scala.concurrent.duration.DurationInt
import fi.spectrumlabs.streaming.Record
import io.circe.syntax._

trait TrackerProgram[S[_]] {
  def run: S[Unit]
}

object TrackerProgram {

  def create[
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F],
    F[_]: Monad,
    C[_]: Foldable
  ](producer: Producer[String, Transaction, S])(
    implicit cache: TrackerCache[F],
    explorer: Explorer[S, F],
    filter: Filter[F]
  ): TrackerProgram[S] = new Impl[S, F, C](producer)

  private final class Impl[S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F], F[
    _
  ]: Monad, C[_]: Foldable](producer: Producer[String, Transaction, S])(
    implicit cache: TrackerCache[F],
    explorer: Explorer[S, F],
    filter: Filter[F]
  ) extends TrackerProgram[S] {

    def run: S[Unit] =
      eval(cache.getLastOffset) >>= exec

    def exec(offset: Int): S[Unit] =
      explorer
        .streamTransactions(offset, 50)
        .groupWithin(10, 10.seconds) // from config
        .evalMap { batch =>
          println(s"Got batch: ${batch.toString}")
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
          cache.setLastOffset(offset + 50).as(offset + 50)
        } >>= exec
  }
}
