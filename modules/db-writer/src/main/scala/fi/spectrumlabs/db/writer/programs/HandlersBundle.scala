package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList

final case class HandlersBundle[S[_]](handlers: NonEmptyList[Handler[S]])

object HandlersBundle {

  def make[S[_]](x: Handler[S], xs: List[Handler[S]]): HandlersBundle[S] =
    HandlersBundle[S](NonEmptyList.of[Handler[S]](x, xs:_*))
}
