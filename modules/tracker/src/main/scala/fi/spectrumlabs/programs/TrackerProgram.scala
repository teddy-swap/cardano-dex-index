package fi.spectrumlabs.programs

import cats.syntax.traverse._
import cats.effect.Timer
import cats.{Functor, FunctorFilter, Monad}
import fi.spectrumlabs.config.TrackerConfig
import fi.spectrumlabs.core.models.{Transaction, TxModel}
import fi.spectrumlabs.repositories.TrackerCache
import fi.spectrumlabs.services.{Explorer, Filter}
import fi.spectrumlabs.streaming.{Producer, QueueStreaming, Record}
import mouse.any._
import tofu.logging.{Logging, Logs}
import tofu.streams.{Compile, Evals}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.compile._
import tofu.syntax.streams.emits._
import tofu.syntax.streams.evals._

import scala.concurrent.duration.DurationInt

trait TrackerProgram[S[_]] {
  def run: S[Unit]
}

object TrackerProgram {

  def create[
    S[_]: Monad: Evals[*[_], F]: FunctorFilter: Compile[*[_], F],
    F[_]: Monad: Timer,
    I[_]: Functor
  ](producer: Producer[String, Transaction, S], config: TrackerConfig)(implicit
    cache: TrackerCache[F],
    explorer: Explorer[S, F],
    queue: QueueStreaming[S, F, Transaction],
    logs: Logs[I, F]
  ): I[TrackerProgram[S]] = ???
//    logs.forService[TrackerProgram[S]].map(implicit __ => new Impl[S, F](producer, config))

//  private final class Impl[S[_]: Monad: Evals[*[_], F]: FunctorFilter: Compile[*[_], F], F[
//    _
//  ]: Monad: Logging: Timer](producer: Producer[String, Transaction, S], config: TrackerConfig)(implicit
//    cache: TrackerCache[F],
//    explorer: Explorer[S, F],
//    queue: QueueStreaming[S, F, TxModel]
//  ) extends TrackerProgram[S] {
//
//    def run: S[Unit] =
//      eval(cache.getLastOffset) >>= exec
//
//    def exec(offset: Long): S[Unit] =
//      eval(info"Current offset is: $offset") >> {
//        eval(
//          explorer
//            .streamTransactions(offset, config.limit)
//            .fold(List.empty[TxModel]) { case (acc, b) => b :: acc }
//        )
//          .flatMap {
//            case Nil => eval(info"Nothing to process. Going to sleep 10 seconds." >> Timer[F].sleep(1.seconds).as(offset)) >>= exec //to config
//            case batch =>
//              eval(batch.traverse(queue.enqueue1) >> info"Going to process next batch.") >>
//              eval(queue.dequeueBatch(config.limit)).evalMap { batch =>
//                val grouped = batch.groupBy(_.isLegacy).toList.map {
//                  case (true, elems) =>
//                    info"Received ${elems.size} legacy txns. Ids are: ${elems.map(_.hash)}. Going to request next batch."
//                  case (false, elems) =>
//
//                }
//                info"Got batch of txn of size ${batch.size}. Last txn id is: ${batch.lastOption.map(_.hash)}."
//                info"Got batch of txn of size ${batch.size}. Last txn id is: ${batch.lastOption.map(_.hash)}."
//                  .as {
//                    batch.filter(Filter.txFilter).map(tx => Record(tx.hash.value, tx))
//                  }
//                  .flatMap { txn =>
//                    (emits[S](txn) |> producer.produce).drain
//                  }
//                  .flatMap { _ =>
//                    val newOffset = batch.length + offset + 1
//                    cache.setLastOffset(newOffset).as(newOffset)
//                  }
//              } >>= exec
//          }
//      }
//  }
}
