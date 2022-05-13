package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList
import cats.syntax.foldable._
import cats.{Foldable, FunctorFilter, Monad}
import fi.spectrumlabs.core.models.{Transaction => Tx}
import fi.spectrumlabs.db.writer.models.{Input, Output, Redeemer, Transaction}
import fi.spectrumlabs.db.writer.persistence.PersistBundle
import fi.spectrumlabs.db.writer.streaming.Consumer
import tofu.streams.{Broadcast, Compile, Evals, Temporal}
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.temporal._

import scala.concurrent.duration.DurationInt

trait Handler[S[_]] {
  def handle: S[Unit]
}

object Handler {

  def create[
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F]: Broadcast,
    F[_]: Monad,
    C[_]: Foldable
  ](consumer: Consumer[_, Tx, S, F], persistBundle: PersistBundle[F]): Handler[S] =
    new Impl[S, F, C](consumer, persistBundle)

  final private class Impl[
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Temporal[*[_], C]: Compile[*[_], F],
    F[_]: Monad,
    C[_]: Foldable
  ](consumer: Consumer[_, Tx, S, F], persistBundle: PersistBundle[F])
    extends Handler[S] {

    def handle: S[Unit] =
      consumer.stream
        .groupWithin(10, 10.seconds) //config
        .flatMap { batch => //safe to nel
          println(s"Going to process batch: ${batch.size}")
          val batchList = NonEmptyList.fromListUnsafe(batch.toList.map(_.message))
          def persist = for {
            _ <- persistBundle.persistTxns.persist(batchList.map(Transaction.fromLedger(_)))
            _ <- persistBundle.persistInputs.persist(
                   NonEmptyList.fromListUnsafe(batchList.toList.flatMap(Input.fromLedger(_)))
                 )
            _ <- persistBundle.persistOutputs.persist(
                   NonEmptyList.fromListUnsafe(batchList.toList.flatMap(Output.fromLedger(_)))
                 )
            _ <- persistBundle.persistRedeemers.persist(
                   NonEmptyList.fromListUnsafe(batchList.toList.flatMap(Redeemer.fromLedger(_)))
                 )
          } yield ()

          eval(persist)
            .map(_ => println(s"Batch processed."))
            .evalMap(_ => batch.toList.lastOption.fold(().pure[F])(_.commit)) //logging
        }
  }
}
