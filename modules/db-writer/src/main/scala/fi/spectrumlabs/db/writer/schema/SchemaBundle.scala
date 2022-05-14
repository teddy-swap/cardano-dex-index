package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models._

final case class SchemaBundle(
  input: Schema[Input],
  output: Schema[Output],
  transaction: Schema[Transaction],
  redeemer: Schema[Redeemer]
)

object SchemaBundle {

  def create: SchemaBundle =
    SchemaBundle(
      new InputSchema,
      new OutputSchema,
      new TransactionSchema,
      new RedeemerSchema
    )
}
