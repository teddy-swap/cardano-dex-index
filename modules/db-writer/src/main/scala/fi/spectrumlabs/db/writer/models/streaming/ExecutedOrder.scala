package fi.spectrumlabs.db.writer.models.streaming

import cats.Show
import fi.spectrumlabs.db.writer.models.orders.TxOutRef
import io.circe.{Decoder, Encoder}
import io.circe.magnolia.derivation.encoder.semiauto._
import io.circe.magnolia.derivation.decoder.semiauto._
import tofu.logging.Loggable

final case class ExecutedOrder[A](
  config: A,
  orderInputId: TxOutRef,
  userOutputId: TxOutRef,
  poolOutputId: TxOutRef,
  poolInputId: TxOutRef
)

object ExecutedOrder {
  implicit def encoder[A: Encoder]: Encoder[ExecutedOrder[A]] =
    deriveMagnoliaEncoder

  implicit def decoder[A: Decoder]: Decoder[ExecutedOrder[A]] =
    deriveMagnoliaDecoder

  implicit def show[A: Show]: Show[ExecutedOrder[A]] =
    Show.show(_.toString)

  implicit def loggable[A: Show]: Loggable[ExecutedOrder[A]] =
    Loggable.show
}
