package fi.spectrumlabs.db.writer.programs

trait WriterProgram[S[_]] {
  def run: S[Unit]
}

object WriterProgram {

}