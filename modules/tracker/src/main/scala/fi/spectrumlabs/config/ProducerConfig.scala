package fi.spectrumlabs.config

import derevo.derive
import derevo.pureconfig.pureconfigReader
import fi.spectrumlabs.streaming.TopicId
import tofu.Context
import tofu.logging.derivation.loggable

@derive(pureconfigReader, loggable)
final case class ProducerConfig(topicId: TopicId, parallelism: Int)

object ProducerConfig extends Context.Companion[ProducerConfig]