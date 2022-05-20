package fi.spectrumlabs.db.writer.config

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.logging.derivation.loggable

import scala.concurrent.duration.FiniteDuration

@derive(loggable, pureconfigReader)
final case class WriterConfig(batchSize: Int, timeout: FiniteDuration, maxConcurrent: Int)
