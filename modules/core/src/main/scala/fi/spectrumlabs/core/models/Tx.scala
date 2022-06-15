package fi.spectrumlabs.core.models

import cats.data.NonEmptyList
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models._

@derive(encoder, decoder)
final case class Tx(
  blockHash: BlockHash,
  blockIndex: Long,
  hash: TxHash,
  inputs: NonEmptyList[Input],
  outputs: NonEmptyList[Output],
  invalidBefore: Option[BigInt],
  invalidHereafter: Option[BigInt],
  metadata: Option[Meta],
  size: Int,
  timestamp: Long
)

object Tx {

  def fromExplorer(tx: Transaction): Option[Tx] =
    for {
      in  <- NonEmptyList.fromList(tx.inputs)
      out <- NonEmptyList.fromList(tx.outputs)
    } yield
      Tx(
        tx.blockHash,
        tx.blockIndex,
        tx.hash,
        in.map(Input.fromExplorer),
        out.map(Output.fromExplorer),
        tx.invalidBefore,
        tx.invalidHereafter,
        tx.metadata.map(Meta.fromExplorer),
        tx.size,
        tx.timestamp
      )
}
