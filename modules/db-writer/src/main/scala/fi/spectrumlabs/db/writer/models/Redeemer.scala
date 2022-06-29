package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.explorer.models.ScriptPurpose
import fi.spectrumlabs.explorer.models.{Bytea, Hash28, TxHash}
import fi.spectrumlabs.db.writer.classes.ToSchema
import io.circe.Json
import fi.spectrumlabs.core.models.Tx

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

  implicit val toSchema: ToSchema[Tx, List[Redeemer]] = (in: Tx) =>
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
