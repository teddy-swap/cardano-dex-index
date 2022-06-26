package fi.spectrumlabs.rates.resolver.gateways

import cats.syntax.option._
import cats.{Functor, Monad}
import fi.spectrumlabs.core.models.domain.AssetClass
import fi.spectrumlabs.rates.resolver.config.NetworkConfig
import fi.spectrumlabs.rates.resolver.models.MetadataResponse
import sttp.client3.circe.asJson
import sttp.client3.{basicRequest, SttpBackend}
import sttp.model.Uri.Segment
import tofu.Throws
import tofu.logging.{Logging, Logs}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import fi.spectrumlabs.rates.resolver.models.{Metadata => Meta}

trait Metadata[F[_]] {

  def getTokenInfo(token: AssetClass): F[Option[Meta]]
}

object Metadata {

  def create[I[_]: Functor, F[_]: Monad: Throws](config: NetworkConfig)(
    implicit
    backend: SttpBackend[F, _],
    logs: Logs[I, F]
  ): I[Metadata[F]] =
    logs.forService[Network[F]].map(implicit __ => new Impl[F](config))

  final private class Impl[F[_]: Monad: Throws: Logging](config: NetworkConfig)(implicit backend: SttpBackend[F, _])
    extends Metadata[F] {

    def getTokenInfo(token: AssetClass): F[Option[Meta]] =
      info"Going to get info for token $token from metadata service" >>
      basicRequest
        .get(
          config.metadataUrl.withPathSegment(Segment(s"metadata/${AssetClass.toMetadata(token)}", identity))
        )
        .response(asJson[MetadataResponse])
        .send(backend)
        .map {
          _.body match {
            case Left(_)      => none
            case Right(value) => value.some
          }
        }
        .flatTap(info => info"Token info for $token is $info")
        .map(_.map { r =>
          Meta(r.decimals, token)
        })
  }
}
