package fi.spectrumlabs.services

import fi.spectrumlabs.core.models.TxEvent

object Filter {

  type TxFilter = TxEvent => Boolean

  def txFilter: TxFilter = _ => true
}
