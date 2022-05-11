package fi.spectrumlabs.db.writer.transformers

import fi.spectrumlabs.core.models.Transaction
import fi.spectrumlabs.db.writer.models.{AnotherExampleData, ExampleData}

trait Transformer[A, B] {
  def transform: A => B
}

object Transformer {

  object instances {

    implicit val TxnToExampleTransformer: Transformer[Transaction, ExampleData] =
      new Transformer[Transaction, ExampleData] {
        def transform: Transaction => ExampleData = txn => ExampleData(txn.blockHash, txn.blockIndex, txn.globalIndex)
      }

    implicit val TxnToAnotherExampleTransformer: Transformer[Transaction, AnotherExampleData] =
      new Transformer[Transaction, AnotherExampleData] {
        def transform: Transaction => AnotherExampleData = txn => AnotherExampleData(txn.hash, txn.globalIndex)
      }
  }

  object syntax {

    implicit final class TransformerOps[A](val value: A) extends AnyVal {
      def unary_~~[B](implicit tr: Transformer[A, B]): B = tr.transform(value)
    }
  }
}
