package fi.spectrumlabs.db.writer.classes

trait FromLedger[A, B] {
  def apply(in: A): B
}
