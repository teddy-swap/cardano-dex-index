package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.TxOutput
import fi.spectrumlabs.explorer.models._
import io.circe.Json
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class Output(
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

object Output {

  def fromExplorer(out: TxOutput): Output =
    out.into[Output].transform
}
