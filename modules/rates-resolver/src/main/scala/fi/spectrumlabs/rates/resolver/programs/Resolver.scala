package fi.spectrumlabs.rates.resolver.programs

import cats.syntax.parallel._
import cats.{Defer, Functor, Monad, Parallel, SemigroupK}
import fi.spectrumlabs.rates.resolver.config.ResolverConfig
import fi.spectrumlabs.rates.resolver.repositories.RatesRepo
import fi.spectrumlabs.rates.resolver.services.ResolverService
import tofu.logging.{Logging, Logs}
import tofu.streams.{Evals, Pace}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.combineK._
import tofu.syntax.streams.evals._
import tofu.syntax.streams.pace._

trait Resolver[S[_]] {
  def run: S[Unit]
}

object Resolver {

  def create[
    I[_]: Functor,
    S[_]: Monad: Evals[*[_], F]: SemigroupK: Defer: Pace,
    F[_]: Monad: Parallel
  ](config: ResolverConfig)(
    implicit
    repo: RatesRepo[F],
    resolver: ResolverService[F],
    logs: Logs[I, F]
  ): I[Resolver[S]] =
    logs.forService[Resolver[S]].map(implicit __ => new Impl[S, F](config))

  final private class Impl[
    S[_]: Monad: Evals[*[_], F]: SemigroupK: Defer: Pace,
    F[_]: Monad: Logging: Parallel
  ](config: ResolverConfig)(
    implicit
    repo: RatesRepo[F],
    resolver: ResolverService[F]
  ) extends Resolver[S] {

    def run: S[Unit] =
      (for {
        _     <- eval(info"Going to update rates.")
        rates <- eval(resolver.resolve)
        _     <- eval(rates.parTraverse(repo.put))
        _     <- eval(info"Rates was updated successfully.")
      } yield ()).repeat
        .throttled(config.throttleRate)
  }
}
