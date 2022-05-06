package fi.spectrumlabs.repositories

import cats.Functor
import dev.profunktor.redis4cats.RedisCommands
import cats.syntax.functor._
import derevo.derive
import tofu.higherKind.derived.representableK

@derive(representableK)
trait TrackerCache[F[_]] {

  def setLastOffset(offset: Int): F[Unit]

  def getLastOffset: F[Int]
}

object TrackerCache {

  def create[I[_]: Functor, F[_]: Functor](implicit redis: RedisCommands[F, String, Int]): TrackerCache[F] =
    new Impl[F]

  private final class Impl[F[_]: Functor](implicit redis: RedisCommands[F, String, Int]) extends TrackerCache[F] {
    def setLastOffset(offset: Int): F[Unit] = redis.set(Key, offset)

    def getLastOffset: F[Int] = redis.get(Key).map(_.getOrElse(0))

    private val Key: String = "tracker-offset-last"
  }
}
