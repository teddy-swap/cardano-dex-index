package fi.spectrumlabs.db.writer.classes

import cats.data.NonEmptyList
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Functor, Monad}
import fi.spectrumlabs.db.writer.persistence.Persist
import mouse.any._
import tofu.logging.{Logging, Logs}
import tofu.syntax.logging._

/** Keeps both FromLedger from A to B and Persist for B.
  * Contains evidence that A can be mapped into B and B can be persisted.
  *
  * Takes batch of T elements, maps them using FromLedger, persists them using Persist
  */

trait Handle[T, F[_]] {
  def handle(in: NonEmptyList[T]): F[Unit]
}

object Handle {

  def createOne[A, B, I[_]: Functor, F[_]: Monad](
    persist: Persist[B, F]
  )(implicit fromLedger: FromLedger[A, B], logs: Logs[I, F]): I[Handle[A, F]] =
    logs.forService[Handle[A, F]].map(implicit __ => new ImplOne[A, B, F](persist))

  def createMany[A, B, I[_]: Functor, F[_]: Monad](persist: Persist[B, F])(
    implicit
    fromLedger: FromLedger[A, List[B]],
    logs: Logs[I, F]
  ): I[Handle[A, F]] =
    logs.forService[Handle[A, F]].map(implicit __ => new ImplMany[A, B, F](persist))

  private final class ImplOne[A, B, F[_]: Monad: Logging](persist: Persist[B, F])(implicit fromLedger: FromLedger[A, B])
    extends Handle[A, F] {

    def handle(in: NonEmptyList[A]): F[Unit] =
      (in.map(fromLedger(_)) |> persist.persist)
        .flatMap(r => info"Finished handle process for $r elements. Batch size was ${in.size}.")
  }

  private final class ImplMany[A, B, F[_]: Monad: Logging](persist: Persist[B, F])(
    implicit
    fromLedger: FromLedger[A, List[B]]
  ) extends Handle[A, F] {

    def handle(in: NonEmptyList[A]): F[Unit] =
      in.toList.flatMap(fromLedger(_)) match {
        case x :: xs =>
          (NonEmptyList.of(x, xs: _*) |> persist.persist)
            .flatMap(r => info"Finished handle process for $r elements. Batch size was ${in.size}.")
        case Nil =>
          info"Nothing to extract. Batch contains 0 elements to persist."
      }
  }
}
