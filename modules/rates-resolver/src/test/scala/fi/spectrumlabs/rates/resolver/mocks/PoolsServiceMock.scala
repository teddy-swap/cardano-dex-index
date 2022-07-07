package fi.spectrumlabs.rates.resolver.mocks

import cats.Applicative
import cats.syntax.applicative._
import fi.spectrumlabs.core.models.domain.Pool
import fi.spectrumlabs.rates.resolver.services.PoolsService

object PoolsServiceMock {

  def create[F[_]: Applicative](pools: List[Pool]): PoolsService[F] = (_: Long) => pools.pure

}
