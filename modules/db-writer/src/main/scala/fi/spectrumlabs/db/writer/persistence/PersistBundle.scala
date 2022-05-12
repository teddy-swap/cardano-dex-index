package fi.spectrumlabs.db.writer.persistence

import cats.FlatMap
import fi.spectrumlabs.core.models.Transaction
import fi.spectrumlabs.db.writer.models.{AnotherExampleData, ExampleData}
import fi.spectrumlabs.db.writer.schema.{Schema, SchemaBundle}
import tofu.doobie.LiftConnectionIO
import fi.spectrumlabs.db.writer.transformers.Transformer.instances._
import tofu.doobie.log.EmbeddableLogHandler
import doobie.implicits._
import doobie._

final case class PersistBundle[D[_]](
  examplePersist: Persist[Transaction, Schema[ExampleData], D],
  anotherExamplePersist: Persist[Transaction, Schema[AnotherExampleData], D]
)

object PersistBundle {

  def create[D[_]: FlatMap: LiftConnectionIO](schema: SchemaBundle)(implicit elh: EmbeddableLogHandler[D]) = {
    import schema._
    PersistBundle(
      Persist.create[Transaction, ExampleData, D](schema.exampleSchema),
      Persist.create[Transaction, AnotherExampleData, D](schema.anotherExampleSchema)
    )
  }
}
