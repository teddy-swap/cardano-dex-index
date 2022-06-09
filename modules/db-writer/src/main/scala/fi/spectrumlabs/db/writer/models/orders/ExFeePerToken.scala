package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExFeePerToken(exFeePerTokenNum: Long, exFeePerTokenDen: Long)
