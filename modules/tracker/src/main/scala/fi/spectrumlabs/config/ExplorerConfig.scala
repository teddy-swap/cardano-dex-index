package fi.spectrumlabs.config

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.Context
import tofu.logging.derivation.loggable

@derive(pureconfigReader, loggable)
final case class ExplorerConfig(url: String)

object ExplorerConfig extends Context.Companion[ExplorerConfig]