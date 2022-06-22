package fi.spectrumlabs.markets.api.services

import cats.data.OptionT
import cats.{Functor, Monad}
import fi.spectrumlabs.core.models.domain.Pool
import fi.spectrumlabs.markets.api.configs.MarketsApiConfig
import fi.spectrumlabs.markets.api.models.PoolInfo
import fi.spectrumlabs.markets.api.repositories.repos.{PoolsRepo, RatesRepo}
import tofu.higherKind.Mid
import tofu.logging.{Logging, Logs}
import tofu.syntax.logging._
import tofu.syntax.monadic._

import scala.concurrent.duration.FiniteDuration

trait AnalyticsService[F[_]] {
  def getPoolInfo(poolId: String, period: FiniteDuration): F[Option[PoolInfo]]
}

object AnalyticsService {

  def create[I[_]: Functor, F[_]: Monad](config: MarketsApiConfig)(
    implicit
    ratesRepo: RatesRepo[F],
    poolsRepo: PoolsRepo[F],
    logs: Logs[I, F]
  ): I[AnalyticsService[F]] = logs.forService[F].map(implicit __ => new Tracing[F] attach new Impl[F](config))

  final private class Impl[F[_]: Monad](config: MarketsApiConfig)(
    implicit
    ratesRepo: RatesRepo[F],
    poolsRepo: PoolsRepo[F]
  ) extends AnalyticsService[F] {

    def getPoolInfo(poolId: String, period: FiniteDuration): F[Option[PoolInfo]] =
      (for {
        poolDb <- OptionT(poolsRepo.getPoolById(poolId, config.minLiquidityValue))
        pool   <- OptionT(Pool.fromDb(poolDb).pure)
        rateX  <- OptionT(ratesRepo.get(pool.x.asset))
        rateY  <- OptionT(ratesRepo.get(pool.y.asset))
        xTvl     = pool.x.amount.value * rateX.rate
        yTvl     = pool.y.amount.value * rateY.rate
        totalTvl = xTvl + yTvl
        (volumeRawX, volumeRawY) <- OptionT(poolsRepo.getPoolVolume(pool, period))
        xVolume     = volumeRawX * rateX.rate
        yVolume     = volumeRawY * rateY.rate
        totalVolume = xVolume + yVolume
      } yield PoolInfo(totalTvl, totalVolume)).value
  }

  final private class Tracing[F[_]: Monad: Logging] extends AnalyticsService[Mid[F, *]] {

    def getPoolInfo(poolId: String, period: FiniteDuration): Mid[F, Option[PoolInfo]] =
      for {
        _ <- trace"Going to get pool info for pool $poolId for period $period"
        r <- _
        _ <- trace"Pool info is $r"
      } yield r
  }
}
