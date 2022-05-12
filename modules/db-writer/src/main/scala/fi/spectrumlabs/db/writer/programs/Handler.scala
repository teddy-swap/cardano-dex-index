package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList
import cats.syntax.foldable._
import cats.{Applicative, Foldable, FunctorFilter, Monad}
import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.persistence.Persist
import fi.spectrumlabs.db.writer.streaming.Consumer
import tofu.streams.{Broadcast, Compile, Evals, Temporal}
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._

import scala.concurrent.duration.DurationInt

trait Handler[S[_]] {
  def handle: S[Unit]
}

object Handler {

  def create[
    T,
    A,
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F]: Broadcast,
    F[_]: Applicative,
    C[_]: Foldable
  ](consumer: Consumer[_, T, S, F], persist: Persist[A, F])(
    implicit
    fromLedger: FromLedger[T, A]
  ): Handler[S] =
    new Impl[T, A, S, F, C](consumer, persist)

  final private class Impl[
    T,
    A,
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F],
    F[_]: Applicative,
    C[_]: Foldable
  ](consumer: Consumer[_, T, S, F], persist: Persist[A, F])(implicit fromLedger: FromLedger[T, A])
    extends Handler[S] {

    def handle: S[Unit] =
      consumer.stream
        .groupWithin(10, 10.seconds) //config
        .flatMap { batch => //safe to nel
          println(s"Going to process batch: ${batch.size}")
          val batchList = NonEmptyList.fromListUnsafe(batch.toList.map(_.message))
          val mapped    = batchList.map(fromLedger(_))
          eval(persist.persist(mapped))
            .map(_ => println(s"Batch processed."))
            .evalMap(_ => batch.toList.lastOption.fold(().pure[F])(_.commit)) //logging
        }
  }
}
