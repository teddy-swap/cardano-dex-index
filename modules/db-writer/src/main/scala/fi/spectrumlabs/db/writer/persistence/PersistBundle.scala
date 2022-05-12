package fi.spectrumlabs.db.writer.persistence

import cats.{~>, Applicative, FlatMap}
import fi.spectrumlabs.db.writer.models.ExampleData
import fi.spectrumlabs.db.writer.schema.SchemaBundle
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr

final case class PersistBundle[F[_]](examplePersist: Persist[ExampleData, F])

object PersistBundle {

  def create[D[_]: FlatMap: LiftConnectionIO, F[_]: Applicative](
    bundle: SchemaBundle,
    xa: D ~> F
  )(implicit elh: EmbeddableLogHandler[D]): PersistBundle[F] =
    PersistBundle(Persist.create[ExampleData, D, F](bundle.exampleSchema, xa))
}
