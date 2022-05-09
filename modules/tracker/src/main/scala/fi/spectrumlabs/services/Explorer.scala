package fi.spectrumlabs.services

import cats.syntax.functor._
import cats.tagless.FunctorK
import fi.spectrumlabs.config.ExplorerConfig
import fi.spectrumlabs.models.Transaction
import fs2.Stream
import io.circe.Json
import io.circe.jawn.CirceSupportParser
import jawnfs2._
import org.typelevel.jawn.Facade
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.{asStreamAlwaysUnsafe, basicRequest, SttpBackend, UriContext}
import sttp.model.Uri.Segment
import tofu.MonadThrow
import tofu.fs2.LiftStream

trait Explorer[S[_], F[_]] {
  def streamTransactions(offset: Int, limit: Int): S[Transaction]
}

object Explorer {

  implicit def functorK[F[_]]: FunctorK[Explorer[*[_], F]] = {
    type Mod[S[_]] = Explorer[S, F]
    cats.tagless.Derive.functorK[Mod]
  }

  def create[S[_]: LiftStream[*[_], F], F[_]: MonadThrow](config: ExplorerConfig)(
    implicit
    backend: SttpBackend[F, Fs2Streams[F]]
  ): Explorer[S, F] =
    functorK.mapK(new Impl(config))(LiftStream[S, F].liftF)

  private final class Impl[F[_]: MonadThrow](config: ExplorerConfig)(implicit backend: SttpBackend[F, Fs2Streams[F]])
    extends Explorer[Stream[F, *], F] {

    implicit private val facade: Facade[Json] = new CirceSupportParser(None, allowDuplicateKeys = false).facade

    def streamTransactions(offset: Int, limit: Int): Stream[F, Transaction] = {
      println(s"Going to request next txns")
      val req =
        basicRequest
          .get(
            uri"${config.url}"
              .withPathSegment(Segment("v1/transactions/stream", identity))
              .addParams("offset" -> s"$offset", "limit" -> s"$limit", "ordering" -> "asc")
          )
          .response(asStreamAlwaysUnsafe(Fs2Streams[F]))
          .send(backend)
          .map(_.body)

      Stream
        .force(req)
        .chunks
        .parseJsonStream
        .map(_.as[Transaction].toOption)
        .unNone
    }
  }

}
