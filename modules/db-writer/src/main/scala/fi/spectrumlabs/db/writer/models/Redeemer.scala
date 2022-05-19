package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.explorer.models.ScriptPurpose
import fi.spectrumlabs.explorer.models.{Bytea, Hash28, TxHash}
import fi.spectrumlabs.db.writer.classes.FromLedger
import io.circe.Json
import fi.spectrumlabs.core.models.TxEvent

final case class Redeemer(
  txHash: TxHash,
  txIndex: Long,
  unitMem: Long,
  unitSteps: Long,
  fee: Long,
  purpose: ScriptPurpose,
  index: Int,
  scriptHash: Hash28,
  data: Option[Json],
  dataBin: Option[Bytea]
)

object Redeemer {

  implicit val fromLedger: FromLedger[TxEvent, List[Redeemer]] = (in: TxEvent) =>
    in.inputs.toList.flatMap { i =>
      i.redeemer.map { r =>
        Redeemer(
          in.hash,
          in.blockIndex,
          r.unitMem,
          r.unitSteps,
          r.fee.toLong,
          r.purpose,
          r.index,
          r.scriptHash,
          r.data,
          r.dataBin
        )
      }
  }
}
