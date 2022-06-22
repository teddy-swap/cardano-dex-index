package fi.spectrumlabs.markets.api.v1

import fi.spectrumlabs.core.network.models.HttpError
import sttp.model.StatusCode
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{emptyOutputAs, endpoint, oneOf, oneOfDefaultMapping, oneOfMapping, Endpoint, EndpointInput}
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import cats.syntax.option._
import io.circe.generic.auto._

package object endpoints {
  val V1Prefix: EndpointInput[Unit] = "v1"

  val baseEndpoint: Endpoint[Unit, HttpError, Unit, Any] =
    endpoint
      .in(V1Prefix)
      .errorOut(
        oneOf[HttpError](
          oneOfMapping(StatusCode.NotFound, jsonBody[HttpError.NotFound].description("not found")),
          oneOfMapping(StatusCode.NoContent, emptyOutputAs(HttpError.NoContent)),
          oneOfDefaultMapping(jsonBody[HttpError.Unknown].description("unknown"))
        )
      )
}
