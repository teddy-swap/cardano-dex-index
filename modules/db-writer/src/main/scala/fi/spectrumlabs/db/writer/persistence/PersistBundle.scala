package fi.spectrumlabs.db.writer.persistence

import cats.{Applicative, FlatMap}
import fi.spectrumlabs.db.writer.models._
import fi.spectrumlabs.db.writer.schema.SchemaBundle
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr

import fi.spectrumlabs.db.writer.schema.jsonPut

import doobie.postgres._
import doobie.postgres.implicits._
import derevo.derive
import doobie._
import doobie.implicits._

final case class PersistBundle[F[_]](
  input: Persist[Input, F],
  output: Persist[Output, F],
  transaction: Persist[Transaction, F],
  redeemer: Persist[Redeemer, F]
)

object PersistBundle {

  def create[D[_]: FlatMap: LiftConnectionIO, F[_]: Applicative](
    implicit
    elh: EmbeddableLogHandler[D],
    txr: Txr[F, D],
    bundle: SchemaBundle
  ): PersistBundle[F] = {
    import bundle._
    PersistBundle(
      Persist.create[Input, D, F](input),
      Persist.create[Output, D, F](output),
      Persist.create[Transaction, D, F](transaction),
      Persist.create[Redeemer, D, F](redeemer)
    )
  }
}
