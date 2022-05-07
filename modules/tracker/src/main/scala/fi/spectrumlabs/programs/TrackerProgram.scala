package fi.spectrumlabs.programs

import fi.spectrumlabs.models.Transaction
import fi.spectrumlabs.repositories.TrackerCache
import fi.spectrumlabs.services.Explorer
import fi.spectrumlabs.streaming.Producer

trait TrackerProgram[F[_]] {
  def run: F[Unit]
}

object TrackerProgram {

  private final class Impl[S[_], F[_]](
    cache: TrackerCache[F],
    explorer: Explorer[S, F],
    producer: Producer[String, Transaction, F]
  ) extends TrackerProgram[F] {
    def run: F[Unit] = ???
  }
}
