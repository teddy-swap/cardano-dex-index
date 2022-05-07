package fi.spectrumlabs

import derevo.derive
import io.estatico.newtype.macros.newtype
import pureconfig.ConfigReader
import tofu.logging.derivation.loggable

package object streaming {

  @derive(loggable)
  @newtype case class TopicId(value: String)

  object TopicId {
    implicit val configReader: ConfigReader[TopicId] = deriving
  }

}
