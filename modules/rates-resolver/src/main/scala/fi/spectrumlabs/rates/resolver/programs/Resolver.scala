package fi.spectrumlabs.rates.resolver.programs

import cats.syntax.either._
import cats.syntax.eq._
import cats.syntax.parallel._
import cats.{Defer, Functor, Monad, Parallel, SemigroupK}
import fi.spectrumlabs.core.{AdaAssetClass, DefaultDecimal}
import fi.spectrumlabs.core.models.domain.Pool
import fi.spectrumlabs.core.models.rates.ResolvedRate
import fi.spectrumlabs.rates.resolver.config.ResolverConfig
import fi.spectrumlabs.rates.resolver.gateways.Network
import fi.spectrumlabs.rates.resolver.repositories.RatesRepo
import fi.spectrumlabs.rates.resolver.services.{MetadataService, PoolsService}
import tofu.logging.{Logging, Logs}
import tofu.streams.{Evals, Pace}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._

import scala.math.BigDecimal.RoundingMode

trait Resolver[S[_]] {
  def run: S[Unit]
}

object Resolver {

  def create[
    I[_]: Functor,
    S[_]: Monad: Evals[*[_], F]: SemigroupK: Defer: Pace,
    F[_]: Monad: Parallel
  ](config: ResolverConfig)(
    implicit
    pools: PoolsService[F],
    repo: RatesRepo[F],
    network: Network[F],
    metadataService: MetadataService[F],
    logs: Logs[I, F]
  ): I[Resolver[S]] =
    logs.forService[Resolver[S]].map(implicit __ => new Impl[S, F](config))

  final private class Impl[
    S[_]: Monad: Evals[*[_], F]: SemigroupK: Defer: Pace,
    F[_]: Monad: Logging: Parallel
  ](config: ResolverConfig)(
    implicit
    pools: PoolsService[F],
    repo: RatesRepo[F],
    network: Network[F],
    metadataService: MetadataService[F]
  ) extends Resolver[S] {

    def run: S[Unit] =
      for {
        _     <- eval(info"Going to update rates.")
        rates <- eval(resolve)
        _     <- eval(rates.parTraverse(repo.put))
        _     <- eval(info"Rates was updated successfully.")
      } yield ()

    def resolve: F[List[ResolvedRate]] =
      network.getAdaPrice
        .flatMap { adaPrice =>
          (for {
            pools <- pools.getAllLatest(config.minLiquidityValue).flatTap(pools => trace"Pools from DB are: $pools.")
            info  <- metadataService.getTokensMeta(pools.flatMap(p => p.x.asset :: p.y.asset :: Nil))
          } yield (pools, info)).map {
            case (pools, info) =>
              val poolsWithAda = pools.filter(_.contains(AdaAssetClass))

              val resolvedByAda =
                poolsWithAda
                  .map { r =>
                    val xDecimal = info.find(_.asset == r.x.asset).map(_.decimals).getOrElse(DefaultDecimal)
                    val yDecimal = info.find(_.asset == r.y.asset).map(_.decimals).getOrElse(DefaultDecimal)
                    ResolvedRate(r, AdaAssetClass, xDecimal, yDecimal)
                  }

              val resolvedViaAda =
                pools
                  .filterNot(_.contains(AdaAssetClass))
                  .flatMap { pool =>
                    Either
                      .catchNonFatal {
                        poolsWithAda
                          .filter(_.contains(pool.x.asset, pool.y.asset))
                          .maxBy { p =>
                            if (p.x.asset === AdaAssetClass)
                              resolvedByAda.find(_.asset === pool.y.asset).map(tvl(p, adaPrice, _))
                            else
                              resolvedByAda.find(_.asset === pool.x.asset).map(tvl(p, _, adaPrice))
                          }
                      }
                      .toOption
                      .flatMap(r => resolvedByAda.find(_.find(r.x.asset, r.y.asset, r.id)))
                      .map { r =>
                        val xDecimal = info.find(_.asset == pool.x.asset).map(_.decimals).getOrElse(DefaultDecimal)
                        val yDecimal = info.find(_.asset == pool.y.asset).map(_.decimals).getOrElse(DefaultDecimal)
                        ResolvedRate(pool, r, xDecimal, yDecimal)
                      }
                  }

              adaPrice :: (resolvedByAda ::: resolvedViaAda)
                .map(rate => rate.copy(rate.asset, rate.rate * adaPrice.rate))
          }
        }
        .flatTap(resolved => info"Resolved rates are: $resolved.")

    private def tvl(pool: Pool, rateX: ResolvedRate, rateY: ResolvedRate): BigDecimal = {
      val xTvl     = pool.x.amount.dropPenny(rateX.decimals) * rateX.rate
      val yTvl     = pool.y.amount.dropPenny(rateY.decimals) * rateY.rate
      val totalTvl = (xTvl + yTvl).setScale(0, RoundingMode.HALF_UP)
      totalTvl
    }
  }
}
