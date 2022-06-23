package fi.spectrumlabs.core.models.domain

import derevo.derive
import doobie.util.Put
import tofu.logging.derivation.loggable

@derive(loggable)
final case class AssetAmount(asset: AssetClass, amount: Amount)
