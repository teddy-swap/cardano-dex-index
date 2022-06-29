package fi.spectrumlabs.markets.api.context

import derevo.derive
import fi.spectrumlabs.markets.api.configs.ConfigBundle
import io.estatico.newtype.ops._
import tofu.WithContext
import tofu.logging.derivation.{hidden, loggable}
import tofu.optics.macros.{promote, ClassyOptics}

@ClassyOptics
@derive(loggable)
final case class AppContext(
  @promote @hidden config: ConfigBundle,
  @promote traceId: TraceId
)

object AppContext extends WithContext.Companion[AppContext] {

  def init(configs: ConfigBundle): AppContext =
    AppContext(configs, "<Root>".coerce[TraceId])
}
