package fi.spectrumlabs.db.writer.classes

import cats.{Applicative, Functor}
import cats.data.NonEmptyList
import cats.syntax.functor._
import cats.syntax.applicative._
import fi.spectrumlabs.db.writer.persistence.Persist
import mouse.any._

trait Handle[T, F[_]] {
  def handle(in: NonEmptyList[T]): F[Unit]
}

object Handle {

  def createOne[A, B, F[_]: Functor](persist: Persist[B, F])(implicit fromLedger: FromLedger[A, B]): Handle[A, F] =
    new ImplOne[A, B, F](persist)

  def createMany[A, B, F[_]: Applicative](persist: Persist[B, F])(
    implicit
    fromLedger: FromLedger[A, List[B]]
  ): Handle[A, F] =
    new ImplMany[A, B, F](persist)

  private final class ImplOne[A, B, F[_]: Functor](persist: Persist[B, F])(implicit fromLedger: FromLedger[A, B])
    extends Handle[A, F] {

    def handle(in: NonEmptyList[A]): F[Unit] =
      (in.map(fromLedger(_)) |> persist.persist).map(r => println(s"Handled $r elems."))
  }

  private final class ImplMany[A, B, F[_]: Applicative](persist: Persist[B, F])(
    implicit
    fromLedger: FromLedger[A, List[B]]
  ) extends Handle[A, F] {

    def handle(in: NonEmptyList[A]): F[Unit] =
      in.toList.flatMap(fromLedger(_)) match {
        case x :: xs =>
          (NonEmptyList.of(x, xs: _*) |> persist.persist)
            .map(r => println(s"Handled $r elems."))
        case Nil => println(s"Nothing to process in handle many").pure[F]
      }
  }
}
