package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.{AnotherExampleData, ExampleData}

abstract class SchemaBundle {
  implicit val exampleSchema: Schema[ExampleData]
  implicit val anotherExampleSchema: Schema[AnotherExampleData]
}

object SchemaBundle {

  def create: SchemaBundle = new SchemaBundle {

    implicit val exampleSchema: Schema[ExampleData] = new ExampleSchema

    implicit val anotherExampleSchema: Schema[AnotherExampleData] = new AnotherExampleSchema
  }
}
