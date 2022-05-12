package fi.spectrumlabs.db.writer.programs

import tofu.streams.{Broadcast, Compile, Emits, ParFlatten}
import tofu.syntax.streams.compile._
import tofu.syntax.streams.emits._
import tofu.syntax.streams.parFlatten._

trait WriterProgram[F[_]] {
  def run: F[Unit]
}

object WriterProgram {

  def create[S[_]: Compile[*[_], F]: Broadcast: Emits: ParFlatten, F[_]](
    handlers: HandlersBundle[S]
  ): WriterProgram[F] = new Impl[S, F](handlers)

  private final class Impl[S[_]: Compile[*[_], F]: Broadcast: Emits: ParFlatten, F[_]](
    handlers: HandlersBundle[S]
  ) extends WriterProgram[F] {

    def run: F[Unit] =
      emits(handlers.handlers.toList.map(_.handle)).parFlatten(2).drain
  }
}
