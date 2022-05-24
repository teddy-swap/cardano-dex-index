package fi.spectrumlabs.core.streaming.config

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.WithContext
import tofu.logging.derivation.loggable

@derive(pureconfigReader, loggable)
final case class KafkaConfig(bootstrapServers: List[String])

object KafkaConfig extends WithContext.Companion[KafkaConfig]
