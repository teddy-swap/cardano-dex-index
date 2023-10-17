package fi.spectrumlabs.db.writer.models.cardano

import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}
import cats.syntax.either._
import derevo.circe.magnolia.decoder
import derevo.derive
import fi.spectrumlabs.db.writer.classes.Key
import cats.syntax.show._
import fi.spectrumlabs.db.writer.models.db.{Deposit, Redeem, Swap}

sealed trait Order {
  val fullTxOut: FullTxOut
}

final case class SwapOrder(fullTxOut: FullTxOut, order: OrderAction[SwapAction], slotNo: Long) extends Order
object SwapOrder {
  implicit val decoder: Decoder[SwapOrder] = new Decoder[SwapOrder] {
    override def apply(c: HCursor): Result[SwapOrder] = {
      c.values.toRight(DecodingFailure("Expected array", c.history)).flatMap { values =>
        values.toList match {
          case List(orderJson, slotNoJson) =>
            for {
              slotNo <- slotNoJson.as[Long]
              orderArray <- orderJson.asArray.toRight(DecodingFailure("Expected inner array", c.history))
              List(fullTxOutJson, orderActionJson) = orderArray.toList
              fullTxOut <- fullTxOutJson.as[FullTxOut]
              orderAction <- orderActionJson.as[OrderAction[SwapAction]]
            } yield SwapOrder(fullTxOut, orderAction, slotNo)
          case _ => Left(DecodingFailure("Expected array of two elements", c.history))
        }
      }
    }
  }
}


final case class DepositOrder(fullTxOut: FullTxOut, order: OrderAction[DepositAction], slotNo: Long) extends Order

object DepositOrder {
  implicit val decoder: Decoder[DepositOrder] = new Decoder[DepositOrder] {
    override def apply(c: HCursor): Result[DepositOrder] = {
      c.values.toRight(DecodingFailure("Expected array", c.history)).flatMap { values =>
        values.toList match {
          case List(orderJson, slotNoJson) =>
            for {
              slotNo <- slotNoJson.as[Long]
              orderArray <- orderJson.asArray.toRight(DecodingFailure("Expected inner array", c.history))
              List(fullTxOutJson, orderActionJson) = orderArray.toList
              fullTxOut <- fullTxOutJson.as[FullTxOut]
              orderAction <- orderActionJson.as[OrderAction[DepositAction]]
            } yield DepositOrder(fullTxOut, orderAction, slotNo)
          case _ => Left(DecodingFailure("Expected array of two elements", c.history))
        }
      }
    }
  }
}

final case class RedeemOrder(fullTxOut: FullTxOut, order: OrderAction[RedeemAction], slotNo: Long) extends Order

object RedeemOrder {
  implicit val decoder: Decoder[RedeemOrder] = new Decoder[RedeemOrder] {
    override def apply(c: HCursor): Result[RedeemOrder] = {
      c.values.toRight(DecodingFailure("Expected array", c.history)).flatMap { values =>
        values.toList match {
          case List(orderJson, slotNoJson) =>
            for {
              slotNo <- slotNoJson.as[Long]
              orderArray <- orderJson.asArray.toRight(DecodingFailure("Expected inner array", c.history))
              List(fullTxOutJson, orderActionJson) = orderArray.toList
              fullTxOut <- fullTxOutJson.as[FullTxOut]
              orderAction <- orderActionJson.as[OrderAction[RedeemAction]]
            } yield RedeemOrder(fullTxOut, orderAction, slotNo)
          case _ => Left(DecodingFailure("Expected array of two elements", c.history))
        }
      }
    }
  }
}

object Order {

  implicit val key: Key[Order] = new Key[Order] {
    override def getKey(in: Order): String = {
      in match {
        case swap: SwapOrder => Swap.SwapRedisPrefix ++ swap.order.action.swapRewardPkh.getPubKeyHash
        case deposit: DepositOrder => Deposit.DepositRedisPrefix ++ deposit.order.action.depositRewardPkh.getPubKeyHash
        case redeem: RedeemOrder => Redeem.RedeemRedisPrefix ++ redeem.order.action.redeemRewardPkh.getPubKeyHash
      }
    }

    override def getExtendedKey(t: Order): String =
      t match {
        case swap: SwapOrder => getKey(t) ++ swap.fullTxOut.fullTxOutRef.show
        case deposit: DepositOrder => getKey(t) ++ deposit.fullTxOut.fullTxOutRef.show
        case redeem: RedeemOrder => getKey(t) ++ redeem.fullTxOut.fullTxOutRef.show
      }
  }

  implicit def commonDecoder: Decoder[Order] = new Decoder[Order] {

    override def apply(c: HCursor): Result[Order] =
      (c.as[SwapOrder]).orElse(c.as[DepositOrder]).orElse(c.as[RedeemOrder])
  }
}
