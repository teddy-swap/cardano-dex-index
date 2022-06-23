package fi.spectrumlabs.markets.api.repositories.sql

import doobie.util.log.LogHandler
import doobie.implicits._
import doobie.util.query.Query0
import fi.spectrumlabs.core.models.db.Pool
import fi.spectrumlabs.core.models.domain.{Pool => DomainPool}
import fi.spectrumlabs.markets.api.models.PoolVolume
import fi.spectrumlabs.markets.api.repositories._
import scala.concurrent.duration.FiniteDuration
import cats.syntax.show._

final class PoolsSql(implicit lh: LogHandler) {

  def getPool(poolId: String, minLiquidityValue: Long): Query0[Pool] =
    sql"""
         |SELECT pool_id, x, reserves_x, y, reserves_y FROM pool p
         |LEFT JOIN (
         |  SELECT pool_id AS pid, max(id) AS id FROM pool GROUP BY pool_id
         |) AS plast ON plast.pid = p.pool_id AND plast.id = p.id
         |WHERE
         |plast.id = p.id AND p.reserves_x >= $minLiquidityValue AND p.reserves_y >= $minLiquidityValue and pool_id = $poolId;
       """.stripMargin.query[Pool]

  def getPoolVolume(pool: DomainPool, period: FiniteDuration): Query0[PoolVolume] =
    sql"""
         |select * from
         |	(select sum(actual_quote) from executed_swap WHERE pool_nft = ${pool.id} and base = ${pool.x.asset.show} and timestamp > ${period.toSeconds}) x
         |	CROSS JOIN
         |	(select sum(actual_quote) from executed_swap WHERE pool_nft = ${pool.id} and base = ${pool.y.asset.show} and timestamp > ${period.toSeconds}) y
       """.stripMargin.query[PoolVolume]
}
