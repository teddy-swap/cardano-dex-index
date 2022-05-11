package fi.spectrumlabs.db.writer.persistence

import cats.FlatMap
import cats.data.NonEmptyList
import cats.tagless.syntax.functorK._
import doobie.ConnectionIO
import doobie.util.log.LogHandler
import fi.spectrumlabs.db.writer.schema.Schema
import fi.spectrumlabs.db.writer.transformers.Transformer
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.higherKind.RepresentableK

trait Persist[A, B, D[_]] {
  def persist(inputs: NonEmptyList[A])(implicit schema: Schema[B], transformer: Transformer[A, B]): D[Int]
}

object Persist {

  implicit def repK[A, B]: RepresentableK[Persist[A, B, *[_]]] = {
    type Repr[F[_]] = Persist[A, B, F]
    tofu.higherKind.derived.genRepresentableK[Repr]
  }

  def create[A, B, D[_]: FlatMap: LiftConnectionIO](implicit elh: EmbeddableLogHandler[D]): Persist[A, B, D] =
    elh.embed(implicit __ => new Impl[A, B]().mapK(LiftConnectionIO[D].liftF))

  private final class Impl[A, B](implicit lh: LogHandler) extends Persist[A, B, ConnectionIO] {

    def persist(
      inputs: NonEmptyList[A]
    )(implicit schema: Schema[B], transformer: Transformer[A, B]): ConnectionIO[Int] = {
      val toInsert = inputs.map(transformer.transform).toList
      schema.insertNoConflict.updateMany(toInsert)
    }
  }
}
