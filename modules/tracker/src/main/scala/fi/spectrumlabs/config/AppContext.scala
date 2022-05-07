package fi.spectrumlabs.config

import derevo.derive
import tofu.Context
import tofu.logging.derivation.{hidden, loggable}
import tofu.optics.macros.{promote, ClassyOptics}

@ClassyOptics
@derive(loggable)
final case class AppContext(
  @promote @hidden config: ConfigBundle,
)

object AppContext extends Context.Companion[AppContext] {

  def init(configs: ConfigBundle): AppContext =
    AppContext(configs)
}
