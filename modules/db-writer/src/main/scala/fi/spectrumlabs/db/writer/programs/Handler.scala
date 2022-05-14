package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList
import cats.syntax.foldable._
import cats.syntax.parallel._
import cats.{Foldable, Monad, Parallel}
import fi.spectrumlabs.core.models.{Transaction => Tx}
import fi.spectrumlabs.db.writer.classes.Handle
import fi.spectrumlabs.db.writer.streaming.Consumer
import tofu.streams.{Evals, Temporal}
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._

import scala.concurrent.duration.DurationInt

trait Handler[S[_]] {
  def handle: S[Unit]
}

object Handler {

  def create[
    S[_]: Monad: Evals[*[_], F]: Temporal[*[_], C],
    F[_]: Monad: Parallel,
    C[_]: Foldable
  ](implicit consumer: Consumer[_, Tx, S, F], handlers: NonEmptyList[Handle[Tx, F]]): Handler[S] =
    new Impl[S, F, C]

  final private class Impl[
    S[_]: Monad: Evals[*[_], F]: Temporal[*[_], C],
    F[_]: Monad: Parallel,
    C[_]: Foldable
  ](implicit consumer: Consumer[_, Tx, S, F], handlers: NonEmptyList[Handle[Tx, F]])
    extends Handler[S] {

    def handle: S[Unit] =
      consumer.stream
        .groupWithin(10, 10.seconds) //config
        .flatMap { batch => //safe to nel
          println(s"Going to process batch: ${batch.size}")
          val batchList = NonEmptyList.fromListUnsafe(batch.toList.map(_.message))
          eval(handlers.toList.parTraverse(_.handle(batchList)))
            .map(_ => println(s"Batch processed."))
            .evalMap(_ => batch.toList.lastOption.fold(().pure[F])(_.commit)) //logging
        }
  }
}
