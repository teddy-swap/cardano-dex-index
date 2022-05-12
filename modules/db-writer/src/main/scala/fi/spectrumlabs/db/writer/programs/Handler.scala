package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList
import cats.{Applicative, Foldable, FunctorFilter, Monad}
import fi.spectrumlabs.db.writer.persistence.Persist
import fi.spectrumlabs.db.writer.streaming.Consumer
import tofu.streams.{Compile, Evals, Temporal}
import mouse.anyf._
import tofu.streams.{Compile, Evals, Temporal}
import tofu.syntax.monadic._
import tofu.syntax.streams.compile._
import tofu.syntax.streams.emits._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._
import cats.syntax.foldable._
import fi.spectrumlabs.db.writer.schema.Schema
import tofu.doobie.transactor.Txr

import scala.concurrent.duration.DurationInt

trait Handler[A, B, S[_]] {
  def handle: S[Unit]
}

object Handler {

  def create[
    A,
    B,
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F],
    F[_]: Applicative,
    D[_],
    C[_]: Foldable
  ](consumer: Consumer[_, A, S, F], persist: Persist[A, Schema[B], D], txr: Txr[F, D]): Handler[A, B, S] =
    new Impl[A, B, S, F, D, C](consumer, persist, txr)

  final private class Impl[
    A,
    B,
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F],
    F[_]: Applicative,
    D[_],
    C[_]: Foldable
  ](consumer: Consumer[_, A, S, F], persist: Persist[A, Schema[B], D], txr: Txr[F, D])
    extends Handler[A, B, S] {

    def handle: S[Unit] =
      consumer.stream
        .groupWithin(10, 10.seconds) //config
        .flatMap { batch => //safe to nel
          println(s"Going to process batch: ${batch.size}")
          eval(persist.persist(NonEmptyList.fromListUnsafe(batch.toList.map(_.message))) ||> txr.trans)
            .map(_ => println(s"Batch processed."))
            .evalMap(_ => batch.toList.lastOption.fold(().pure[F])(_.commit)) //logging
        }
  }
}
