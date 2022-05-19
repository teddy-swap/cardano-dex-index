package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.TxOutput
import fi.spectrumlabs.explorer.models._
import io.circe.Json
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class OutputEvent(
  ref: OutRef,
  blockHash: BlockHash,
  txHash: TxHash,
  index: Int,
  addr: Addr,
  rawAddr: Bytea,
  paymentCred: Option[PaymentCred],
  value: List[OutAsset],
  dataHash: Option[Hash32],
  data: Option[Json],
  dataBin: Option[Bytea],
  spentByTxHash: Option[TxHash]
)

object OutputEvent {

  def fromExplorer(out: TxOutput): OutputEvent =
    out.into[OutputEvent].transform
}
