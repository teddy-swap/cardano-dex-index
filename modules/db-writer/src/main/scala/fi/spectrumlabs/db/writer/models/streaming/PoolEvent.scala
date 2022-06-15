package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders.Pool
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, show, loggable)
final case class PoolEvent(pool: Pool, timestamp: Long)
