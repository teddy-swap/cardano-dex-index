package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, show, loggable)
final case class PoolFee( poolFeeDen: Long, poolFeeNum: Long)
