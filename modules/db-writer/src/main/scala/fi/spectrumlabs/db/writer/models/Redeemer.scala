package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.core.models.ScriptPurpose
import fi.spectrumlabs.core.models.models.{Bytea, Hash28, TxHash}
import fi.spectrumlabs.db.writer.classes.FromLedger
import io.circe.Json
import fi.spectrumlabs.core.models.{Transaction => Tx}

final case class Redeemer(
  txHash: TxHash,
  txIndex: Long,
  unitMem: Long,
  unitSteps: Long,
  fee: BigInt,
  purpose: ScriptPurpose,
  index: Int,
  scriptHash: Hash28,
  data: Option[Json],
  dataBin: Option[Bytea]
)

object Redeemer {

  implicit val fromLedger: FromLedger[Tx, List[Redeemer]] = (in: Tx) =>
    in.inputs.flatMap { i =>
      i.redeemer.map { r =>
        Redeemer(
          in.hash,
          in.blockIndex,
          r.unitMem,
          r.unitSteps,
          r.fee,
          r.purpose,
          r.index,
          r.scriptHash,
          r.data,
          r.dataBin
        )
      }
    }
}
