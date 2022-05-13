package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.core.models.models.{OutRef, TxHash}
import fi.spectrumlabs.core.models.{Transaction => Tx}
import fi.spectrumlabs.db.writer.classes.FromLedger

final case class Input(txHash: TxHash, txIndex: Long, outFef: OutRef, outIndex: Int, redeemerIndex: Option[Int])

object Input {

  implicit val fromLedger: FromLedger[Tx, List[Input]] = (in: Tx) =>
    in.inputs.map { i =>
      Input(
        in.hash,
        in.blockIndex,
        i.out.ref,
        i.out.index,
        i.redeemer.map(_.index)
      )
    }
}
