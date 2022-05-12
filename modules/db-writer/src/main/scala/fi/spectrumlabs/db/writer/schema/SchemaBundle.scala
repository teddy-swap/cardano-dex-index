package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.ExampleData

final case class SchemaBundle(exampleSchema: Schema[ExampleData])

object SchemaBundle {

  def create: SchemaBundle = SchemaBundle(new ExampleSchema)
}
