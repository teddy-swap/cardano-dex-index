package fi.spectrumlabs.config

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.WithContext
import tofu.logging.derivation.loggable
import tofu.optics.macros.{promote, ClassyOptics}

@ClassyOptics
@derive(loggable, pureconfigReader)
final case class ConfigBundle(
  explorer: ExplorerConfig,
  tracker: TrackerConfig,
  redis: RedisConfig,
  producer: ProducerConfig,
  @promote kafka: KafkaConfig
)

object ConfigBundle extends WithContext.Companion[ConfigBundle] with ConfigBundleCompanion[ConfigBundle]
