package fi.spectrumlabs.db.writer.persistence

import cats.FlatMap
import cats.data.NonEmptyList
import cats.tagless.syntax.functorK._
import doobie.ConnectionIO
import doobie.util.Write
import doobie.util.log.LogHandler
import fi.spectrumlabs.db.writer.schema.Schema
import fi.spectrumlabs.db.writer.transformers.Transformer
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.higherKind.RepresentableK

trait Persist[A, B, D[_]] {
  def persist(inputs: NonEmptyList[A]): D[Int]
}

object Persist {

  implicit def repK[A, B]: RepresentableK[Persist[A, B, *[_]]] = {
    type Repr[F[_]] = Persist[A, B, F]
    tofu.higherKind.derived.genRepresentableK[Repr]
  }

  def create[A, B: Write, D[_]: FlatMap: LiftConnectionIO](schema: Schema[B])(
    implicit
    transformer: Transformer[A, B],
    elh: EmbeddableLogHandler[D]
  ): Persist[A, Schema[B], D] =
    elh.embed(implicit __ => new Impl[A, B](schema).mapK(LiftConnectionIO[D].liftF))

  private final class Impl[A, B: Write](schema: Schema[B])(implicit transformer: Transformer[A, B], lh: LogHandler)
    extends Persist[A, Schema[B], ConnectionIO] {

    def persist(
      inputs: NonEmptyList[A]
    ): ConnectionIO[Int] = {
      val toInsert = inputs.map(transformer.transform).toList
      schema.insertNoConflict.updateMany(toInsert)
    }
  }
}
