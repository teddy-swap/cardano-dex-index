package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.ToSchema
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import fi.spectrumlabs.db.writer.models.orders.AssetClass.syntax._

final case class Pool(
  id: Coin,
  reservesX: Amount,
  reservesY: Amount,
  liquidity: Amount,
  x: Coin,
  y: Coin,
  lq: Coin,
  poolFeeNum: Long,
  poolFeeDen: Long,
  outCollateral: Amount,
  outputId: TxOutRef,
  timestamp: Long
)

object Pool {

  implicit val toSchema: ToSchema[streaming.PoolEvent, Pool] =
    (in: streaming.PoolEvent) =>
      Pool(
        in.pool.id.toCoin,
        in.pool.reservesX,
        in.pool.reservesY,
        in.pool.liquidity,
        in.pool.x.toCoin,
        in.pool.y.toCoin,
        in.pool.lq.toCoin,
        in.pool.fee.poolFeeNum,
        in.pool.fee.poolFeeDen,
        in.pool.outCollateral,
        in.outputId,
        in.timestamp
    )

}
