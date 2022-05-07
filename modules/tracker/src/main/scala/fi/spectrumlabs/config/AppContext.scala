package fi.spectrumlabs.config

import derevo.derive
import tofu.WithContext
import tofu.logging.derivation.{hidden, loggable}
import tofu.optics.macros.{ClassyOptics, promote}

@ClassyOptics
@derive(loggable)
final case class AppContext(
                             @promote @hidden config: ConfigBundle,
                           )

object AppContext extends WithContext.Companion[AppContext] {

  def init(configs: ConfigBundle): AppContext =
    AppContext(configs)
}