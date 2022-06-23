package fi.spectrumlabs.markets.api.v1.endpoints

import fi.spectrumlabs.core.network.models.HttpError
import fi.spectrumlabs.markets.api.models.PoolInfo
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import fi.spectrumlabs.markets.api.v1.endpoints._

import scala.concurrent.duration.FiniteDuration

object PoolInfoEndpoints {

  val pathPrefix = "pool"

  def endpoints: List[Endpoint[_, _, _, _]] = getPoolInfo :: Nil

  def getPoolInfo: Endpoint[(String, FiniteDuration), HttpError, PoolInfo, Any] =
    baseEndpoint.get
      .in(pathPrefix / "info")
      .in(path[String].description("Pool id"))
      .in(period)
      .out(jsonBody[PoolInfo])
      .tag(pathPrefix)
      .name("Info by pool id")
      .description("Allow to get info about pool within period")
}
