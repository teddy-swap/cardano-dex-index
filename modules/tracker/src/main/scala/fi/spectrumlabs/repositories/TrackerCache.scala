package fi.spectrumlabs.repositories

import cats.Functor
import cats.syntax.functor._
import derevo.derive
import dev.profunktor.redis4cats.RedisCommands
import tofu.higherKind.derived.representableK

@derive(representableK)
trait TrackerCache[F[_]] {

  def setLastOffset(offset: Long): F[Unit]

  def getLastOffset: F[Long]
}

object TrackerCache {

  def create[I[_]: Functor, F[_]: Functor](implicit redis: RedisCommands[F, String, Long]): TrackerCache[F] =
    new Impl[F]

  private final class Impl[F[_]: Functor](implicit redis: RedisCommands[F, String, Long]) extends TrackerCache[F] {
    def setLastOffset(offset: Long): F[Unit] = redis.set(Key, offset)

    def getLastOffset: F[Long] = redis.get(Key).map(_.getOrElse(0))

    private val Key: String = "tracker-offset-last"
  }
}
