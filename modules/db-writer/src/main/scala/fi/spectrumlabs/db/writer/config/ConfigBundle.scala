package fi.spectrumlabs.db.writer.config

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.WithContext
import tofu.logging.derivation.loggable
import tofu.optics.macros.ClassyOptics

@ClassyOptics
@derive(loggable, pureconfigReader)
final case class ConfigBundle(pg: PgConfig, tnxConsumer: ConsumerConfig, kafka: KafkaConfig)

object ConfigBundle extends WithContext.Companion[ConfigBundle] with ConfigBundleCompanion[ConfigBundle]
