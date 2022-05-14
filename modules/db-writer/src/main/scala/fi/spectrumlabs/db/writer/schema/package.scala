package fi.spectrumlabs.db.writer

import cats.data.NonEmptyList
import doobie.Put
import io.circe.Json
import org.postgresql.util.PGobject

package object schema {

  implicit val jsonPut: Put[Json] =
    Put.Advanced.other[PGobject](NonEmptyList.of("JSONB")).tcontramap[Json] { j =>
      val o = new PGobject
      o.setType("JSONB")
      o.setValue(j.noSpaces)
      o
    }
}
