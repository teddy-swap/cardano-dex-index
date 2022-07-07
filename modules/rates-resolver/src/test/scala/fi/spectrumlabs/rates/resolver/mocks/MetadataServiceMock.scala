package fi.spectrumlabs.rates.resolver.mocks

import cats.Applicative
import cats.syntax.applicative._
import fi.spectrumlabs.core.models.domain.AssetClass
import fi.spectrumlabs.rates.resolver.models.Metadata
import fi.spectrumlabs.rates.resolver.services.MetadataService

object MetadataServiceMock {

  def create[F[_]: Applicative](assets: List[(AssetClass, Int)]): MetadataService[F] =
    (_: List[AssetClass]) => assets.map { case (asset, decimals) => Metadata(decimals, asset) }.pure
}
