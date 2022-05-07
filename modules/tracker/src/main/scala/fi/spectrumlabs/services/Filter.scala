package fi.spectrumlabs.services

import cats.Applicative
import fi.spectrumlabs.models.Transaction
import cats.syntax.applicative._

trait Filter[F[_]] {
  def filter(txn: Transaction): F[Boolean]

  def filter(txns: List[Transaction]): F[List[Transaction]]
}

object Filter {

  def create[F[_]: Applicative]: Filter[F] = new Impl[F]

  private final class Impl[F[_]: Applicative] extends Filter[F] {
    def filter(txn: Transaction): F[Boolean] = true.pure[F]

    def filter(txns: List[Transaction]): F[List[Transaction]] = txns.pure[F]
  }
}
