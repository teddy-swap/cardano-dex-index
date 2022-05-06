package fi.spectrumlabs.services

import fi.spectrumlabs.models.Transaction

trait ExplorerService[S[_], F[_]] {
  def streamTransactions(from: Int, limit: Int): S[Transaction]
}

object ExplorerService {


  private final class Impl[S[_], F[_]](implicit backend: SttpBackend[F, _])
}