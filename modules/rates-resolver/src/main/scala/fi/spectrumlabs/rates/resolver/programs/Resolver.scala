package fi.spectrumlabs.rates.resolver.programs

import cats.syntax.parallel._
import cats.{Defer, Functor, Monad, Parallel, SemigroupK}
import fi.spectrumlabs.core.models.rates.ResolvedRate
import fi.spectrumlabs.rates.resolver.{AdaAssetClass, AdaMetadata, DefaultDecimal}
import fi.spectrumlabs.rates.resolver.config.ResolverConfig
import fi.spectrumlabs.rates.resolver.gateways.Network
import fi.spectrumlabs.rates.resolver.repositories.RatesRepo
import fi.spectrumlabs.rates.resolver.services.{MetadataService, PoolsService}
import tofu.logging.{Logging, Logs}
import tofu.streams.{Evals, Pace}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._

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
            info  <- metadataService.getTokensInfo(pools.flatMap(p => p.x.asset :: p.y.asset :: Nil))
          } yield (pools, info)).map {
            case (pools, info) =>
              val resolvedByAda =
                pools
                  .filter(_.contains(AdaAssetClass))
                  .map { r =>
                    val xDecimal = info.find(_.asset == r.x.asset).map(_.decimals).getOrElse(DefaultDecimal)
                    val yDecimal = info.find(_.asset == r.y.asset).map(_.decimals).getOrElse(DefaultDecimal)
                    ResolvedRate(r, AdaAssetClass, xDecimal, yDecimal)
                  }

              val resolvedViaAda =
                pools
                  .filterNot(_.contains(AdaAssetClass))
                  .flatMap { pool =>
                    resolvedByAda
                      .find(_.contains(pool.x.asset, pool.y.asset))
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
  }
}
