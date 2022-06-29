package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.core.models.domain.{AssetAmount, AssetClass}
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, show, loggable)
final case class Deposit(
  poolId: AssetClass,
  x: AssetAmount,
  y: AssetAmount,
  exFee: ExFee,
  rewardPkh: PublicKeyHash,
  rewardSPkh: Option[StakePKH],
  adaCollateral: Long
)
