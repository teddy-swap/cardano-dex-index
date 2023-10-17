package fi.spectrumlabs.db.writer.models.cardano

import io.circe.{Decoder, DecodingFailure, HCursor}
import cats.syntax.either._

final case class Confirmed[A](txOut: FullTxOut, element: A, slotNo: Long)

object Confirmed {
  implicit def decoder[A: Decoder]: Decoder[Confirmed[A]] = new Decoder[Confirmed[A]] {
    override def apply(c: HCursor): Decoder.Result[Confirmed[A]] = {
      c.values.toRight(DecodingFailure("Expected array", c.history)).flatMap { jsons =>
        jsons.toList match {
          case List(innerArrayJson, slotNoJson) =>
            for {
              innerArray <- innerArrayJson.asArray.toRight(DecodingFailure("Expected inner array", c.history))
              List(txOutJson, elementJson) = innerArray.toList
              txOut <- txOutJson.as[FullTxOut]
              element <- elementJson.as[A]
              slotNo <- slotNoJson.as[Long]
            } yield Confirmed(txOut, element, slotNo)
          case _ => Left(DecodingFailure("Expected array of two elements", c.history))
        }
      }
    }
  }
}
