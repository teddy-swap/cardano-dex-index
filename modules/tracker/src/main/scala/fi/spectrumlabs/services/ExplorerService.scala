package fi.spectrumlabs.services

import cats.syntax.functor._
import cats.tagless.FunctorK
import fi.spectrumlabs.models.Transaction
import fs2.Stream
import io.circe.Json
import io.circe.jawn.CirceSupportParser
import jawnfs2._
import org.typelevel.jawn.Facade
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.{SttpBackend, UriContext, asStreamAlwaysUnsafe, basicRequest}
import tofu.fs2.LiftStream
import tofu.kernel.types.MonadThrow

trait ExplorerService[S[_], F[_]] {
  def streamTransactions(offset: Int, limit: Int): S[Transaction]
}

object ExplorerService {

  implicit def functorK[F[_]]: FunctorK[ExplorerService[*[_], F]] = {
    type Mod[S[_]] = ExplorerService[S, F]
    cats.tagless.Derive.functorK[Mod]
  }

  def create[S[_]: LiftStream[*[_], F], F[_]: MonadThrow](implicit
    backend: SttpBackend[F, Fs2Streams[F]]
  ): ExplorerService[S, F] =
    functorK.mapK(new Impl)(LiftStream[S, F].liftF)

  private final class Impl[F[_]: MonadThrow](implicit backend: SttpBackend[F, Fs2Streams[F]])
    extends ExplorerService[Stream[F, *], F] {

    implicit private val facade: Facade[Json] = new CirceSupportParser(None, allowDuplicateKeys = false).facade

    // https://testnet-api.quickblue.io/v1/transactions/stream?offset=0&limit=500&ordering=asc
    def streamTransactions(offset: Int, limit: Int): Stream[F, Transaction] = {
      val req =
        basicRequest
          .get(
            uri"https://testnet-api.quickblue.io/v1/transactions/stream"
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
