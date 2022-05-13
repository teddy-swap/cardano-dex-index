package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.core.models.models._
import fi.spectrumlabs.core.models.{Transaction => Tx}
import fi.spectrumlabs.db.writer.classes.FromLedger
import io.circe.Json
import io.circe.syntax._

final case class Output(
  txHash: TxHash,
  txIndex: Long,
  ref: OutRef,
  blockHash: BlockHash,
  index: Int,
  addr: Addr,
  rawAddr: Bytea,
  paymentCred: Option[PaymentCred],
  value: Json,
  dataHash: Option[Hash32],
  data: Option[Json],
  dataBin: Option[Bytea],
  spentByTxHash: Option[TxHash]
)

object Output {

  implicit val fromLedger: FromLedger[Tx, List[Output]] = (in: Tx) =>
    in.outputs.map { o =>
      Output(
        in.hash,
        in.blockIndex,
        o.ref,
        o.blockHash,
        o.index,
        o.addr,
        o.rawAddr,
        o.paymentCred,
        o.value.asJson,
        o.dataHash,
        o.data,
        o.dataBin,
        o.spentByTxHash
      )
    }

}
