package fi.spectrumlabs.db.writer.models

import cats.data.NonEmptyList
import fi.spectrumlabs.explorer.models._
import fi.spectrumlabs.core.models.TxEvent
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

  implicit val fromLedger: FromLedger[TxEvent, NonEmptyList[Output]] = (in: TxEvent) =>
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
