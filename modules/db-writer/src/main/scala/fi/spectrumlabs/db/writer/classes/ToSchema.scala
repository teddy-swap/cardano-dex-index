package fi.spectrumlabs.db.writer.classes

/** Transform entity A into B.
  * A is one of the cardano ledger models and B is a model to insert into indexes database
  */
trait ToSchema[A, B] {
  def apply(in: A): B
}
