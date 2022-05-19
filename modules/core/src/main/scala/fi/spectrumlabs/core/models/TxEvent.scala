package fi.spectrumlabs.core.models

import cats.data.NonEmptyList
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models._

@derive(encoder, decoder)
final case class TxEvent(
  blockHash: BlockHash,
  blockIndex: Long,
  hash: TxHash,
  inputs: NonEmptyList[InputEvent],
  outputs: NonEmptyList[OutputEvent],
  invalidBefore: Option[BigInt],
  invalidHereafter: Option[BigInt],
  metadata: Option[MetadataEvent],
  size: Int
)

object TxEvent {

  def fromExplorer(tx: Transaction): Option[TxEvent] =
    for {
      in  <- NonEmptyList.fromList(tx.inputs)
      out <- NonEmptyList.fromList(tx.outputs)
    } yield
      TxEvent(
        tx.blockHash,
        tx.blockIndex,
        tx.hash,
        in.map(InputEvent.fromExplorer),
        out.map(OutputEvent.fromExplorer),
        tx.invalidBefore,
        tx.invalidHereafter,
        tx.metadata.map(MetadataEvent.fromExplorer),
        tx.size
      )
}
