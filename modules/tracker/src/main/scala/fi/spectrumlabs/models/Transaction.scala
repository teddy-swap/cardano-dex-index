package fi.spectrumlabs.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(encoder, decoder)
final case class Transaction(id: String)
