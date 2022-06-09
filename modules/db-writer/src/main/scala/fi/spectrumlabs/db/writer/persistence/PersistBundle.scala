package fi.spectrumlabs.db.writer.persistence

import cats.{Applicative, FlatMap}
import fi.spectrumlabs.db.writer.models._
import fi.spectrumlabs.db.writer.models.db.Deposit
import fi.spectrumlabs.db.writer.schema.Schema._
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import fi.spectrumlabs.db.writer.schema._

final case class PersistBundle[F[_]](
  input: Persist[Input, F],
  output: Persist[Output, F],
  transaction: Persist[Transaction, F],
  redeemer: Persist[Redeemer, F],
  executedDeposit: Persist[Deposit, F]
)

object PersistBundle {

  def create[D[_]: FlatMap: LiftConnectionIO, F[_]: Applicative](
    implicit
    elh: EmbeddableLogHandler[D],
    txr: Txr[F, D]
  ): PersistBundle[F] =
    PersistBundle(
      Persist.create[Input, D, F](input),
      Persist.create[Output, D, F](output),
      Persist.create[Transaction, D, F](transaction),
      Persist.create[Redeemer, D, F](redeemer),
      Persist.create[Deposit, D, F](executedDeposit)
    )
}
