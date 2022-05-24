package fi.spectrumlabs.db.writer.programs

import cats.data.NonEmptyList

final case class HandlersBundle[S[_]](handlers: NonEmptyList[Handler[S]])
