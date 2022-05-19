package fi.spectrumlabs.explorer.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(encoder, decoder)
final case class Transaction(
  blockHash: BlockHash,
  blockIndex: Long,
  globalIndex: Long,
  hash: TxHash,
  inputs: List[TxInput],
  outputs: List[TxOutput],
  invalidBefore: Option[BigInt],
  invalidHereafter: Option[BigInt],
  metadata: Option[Metadata],
  size: Int
)
