package fi.spectrumlabs.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.Json

@derive(encoder, decoder)
final case class TxOutput(
  ref: OutRef,
  blockHash: BlockHash,
  txHash: TxHash,
  index: Int,
  globalIndex: Long,
  addr: Addr,
  rawAddr: Bytea,
  paymentCred: Option[PaymentCred],
  value: List[OutAsset],
  dataHash: Option[Hash32],
  data: Option[Json],
  dataBin: Option[Bytea],
  spentByTxHash: Option[TxHash]
)
