package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList

final case class HandlersBundle[S[_]](handlers: NonEmptyList[Handler[S]])

object HandlersBundle {

  def make[S[_]](x: Handler[S], xs: Handler[S]): HandlersBundle[S] =
    HandlersBundle[S](NonEmptyList.of(x, xs))
}
