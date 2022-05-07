package fi.spectrumlabs

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.estatico.newtype.macros.newtype
import tofu.logging.derivation.loggable

package object models {

  @derive(loggable, encoder, decoder)
  @newtype case class BlockHash(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class TxHash(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class OutRef(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class Bytea(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class PaymentCred(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class PolicyId(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class Asset32(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class Hash32(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class Hash28(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class Addr(value: String)

  @newtype final case class Offset(value: Int)
}
