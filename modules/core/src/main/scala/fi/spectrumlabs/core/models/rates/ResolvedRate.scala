package fi.spectrumlabs.core.models.rates

import fi.spectrumlabs.core.models.domain.{AssetClass, Pool}
import cats.syntax.eq._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

@derive(loggable, encoder, decoder)
final case class ResolvedRate(asset: AssetClass, rate: BigDecimal, decimals: Int) {

  def contains(x: AssetClass, y: AssetClass): Boolean =
    asset === x || asset == y
}

object ResolvedRate {

  def apply(pool: Pool, by: AssetClass, xDecimal: Int, yDecimal: Int): ResolvedRate = {
    val xAppliedDecimals = pool.x.amount.dropPenny(xDecimal)
    val yAppliedDecimals = pool.y.amount.dropPenny(yDecimal)
    if (pool.x.asset === by)
      ResolvedRate(
        pool.y.asset,
        xAppliedDecimals / yAppliedDecimals,
        yDecimal
      )
    else
      ResolvedRate(
        pool.x.asset,
        yAppliedDecimals / xAppliedDecimals,
        xDecimal
      )
  }

  def apply(pool: Pool, rate: ResolvedRate, xDecimal: Int, yDecimal: Int): ResolvedRate = {
    val xAppliedDecimals = pool.x.amount.dropPenny(xDecimal)
    val yAppliedDecimals = pool.y.amount.dropPenny(yDecimal)
    if (pool.x.asset === rate.asset)
      ResolvedRate(pool.y.asset, (xAppliedDecimals * yAppliedDecimals) * rate.rate, yDecimal)
    else ResolvedRate(pool.x.asset, (yAppliedDecimals * xAppliedDecimals) * rate.rate, xDecimal)
  }
}
