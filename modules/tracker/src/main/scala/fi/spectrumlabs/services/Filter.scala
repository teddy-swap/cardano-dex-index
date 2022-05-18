package fi.spectrumlabs.services

import fi.spectrumlabs.core.models.Transaction

object Filter {

  type TxFilter = Transaction => Boolean

  def txFilter: TxFilter = _ => true
}
