package fi.spectrumlabs.markets.api

import doobie.util.Put

import scala.concurrent.duration.FiniteDuration

package object repositories {
  implicit val putFiniteDuration: Put[FiniteDuration] = implicitly[Put[Long]].contramap(_.toSeconds)
}
