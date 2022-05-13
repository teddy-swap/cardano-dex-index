package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models._

final case class SchemaBundle(
  inputSchema: Schema[Input],
  outputSchema: Schema[Output],
  txnSchema: Schema[Transaction],
  redeemerSchema: Schema[Redeemer]
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
