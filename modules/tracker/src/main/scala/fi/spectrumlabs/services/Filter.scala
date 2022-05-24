package fi.spectrumlabs.services

import fi.spectrumlabs.core.models.Tx

object Filter {

  type TxFilter = Tx => Boolean

  def txFilter: TxFilter = _ => true
}
