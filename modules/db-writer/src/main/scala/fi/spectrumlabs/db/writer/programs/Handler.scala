package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList
import cats.syntax.foldable._
import cats.syntax.parallel._
import cats.{Foldable, Functor, Monad, Parallel}
import fi.spectrumlabs.core.streaming.Consumer
import fi.spectrumlabs.db.writer.classes.Handle
import fi.spectrumlabs.db.writer.config.WriterConfig
import tofu.logging.{Logging, Logs}
import tofu.streams.{Evals, Temporal}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._

trait Handler[S[_]] {
  def handle: S[Unit]
}

object Handler {

  def create[
    A,
    S[_]: Monad: Evals[*[_], F]: Temporal[*[_], C],
    F[_]: Monad: Parallel,
    C[_]: Foldable,
    I[_]: Functor
  ](
    config: WriterConfig
  )(
    implicit consumer: Consumer[_, Option[A], S, F],
    handlers: NonEmptyList[Handle[A, F]],
    logs: Logs[I, F]
  ): I[Handler[S]] =
    logs.forService[Handler[S]].map(implicit __ => new Impl[A, S, F, C](config))

  final private class Impl[
    A,
    S[_]: Monad: Evals[*[_], F]: Temporal[*[_], C],
    F[_]: Monad: Parallel: Logging,
    C[_]: Foldable
  ](config: WriterConfig)(implicit consumer: Consumer[_, Option[A], S, F], handlers: NonEmptyList[Handle[A, F]])
    extends Handler[S] {

    def handle: S[Unit] =
      consumer.stream
        .groupWithin(config.batchSize, config.timeout)
        .flatMap { batch =>
          batch.toList.flatMap(_.message) match {
            case x :: xs =>
              val nel = NonEmptyList.of(x, xs: _*)
              eval(handlers.toList.parTraverse(_.handle(nel)))
                .evalMap(_ => info"Handler processed next batch of size ${nel.size}.")
                .evalMap(_ => batch.toList.lastOption.fold(().pure[F])(_.commit))
            case Nil =>
              eval(info"Got empty batch in handler. Skip insertion.")
          }
        }
  }
}
