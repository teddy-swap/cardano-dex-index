package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList
import fi.spectrumlabs.core.models.Transaction
import fi.spectrumlabs.db.writer.models.{AnotherExampleData, ExampleData}
import fi.spectrumlabs.db.writer.persistence.PersistBundle
import fi.spectrumlabs.db.writer.streaming.Consumer
import tofu.streams.{Broadcast, Compile, Emits, ParFlatten}
import tofu.syntax.streams.emits._
import tofu.syntax.streams.parFlatten._
import tofu.syntax.streams.compile._
import tofu.syntax.streams.evals._

trait WriterProgram[F[_]] {
  def run: F[Unit]
}

object WriterProgram {

  def create[S[_]: Compile[*[_], F]: Broadcast: Emits: ParFlatten, F[_]](
    handler1: Handler[Transaction, ExampleData, S]
  ): WriterProgram[F] = new Impl[S, F](handler1)

  private final class Impl[S[_]: Compile[*[_], F]: Broadcast: Emits: ParFlatten, F[_]](
    handler1: Handler[Transaction, ExampleData, S]
  ) extends WriterProgram[F] {

    def run: F[Unit] =
      emits(
        List(
          handler1.handle
        )
      ).parFlatten(2).drain

  }
}
