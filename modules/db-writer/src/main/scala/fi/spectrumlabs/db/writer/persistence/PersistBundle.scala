package fi.spectrumlabs.db.writer.persistence

import cats.{~>, Applicative, FlatMap}
import fi.spectrumlabs.db.writer.models._
import fi.spectrumlabs.db.writer.schema.SchemaBundle
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler

final case class PersistBundle[F[_]](
  persistInputs: Persist[Input, F],
  persistOutputs: Persist[Output, F],
  persistTxns: Persist[Transaction, F],
  persistRedeemers: Persist[Redeemer, F]
)

object PersistBundle {

  def create[D[_]: FlatMap: LiftConnectionIO, F[_]: Applicative](
    bundle: SchemaBundle,
    xa: D ~> F
  )(implicit elh: EmbeddableLogHandler[D]): PersistBundle[F] =
    PersistBundle(
      Persist.create[Input, D, F](bundle.inputSchema, xa),
      Persist.create[Output, D, F](bundle.outputSchema, xa),
      Persist.create[Transaction, D, F](bundle.txnSchema, xa),
      Persist.create[Redeemer, D, F](bundle.redeemerSchema, xa)
    )
}
