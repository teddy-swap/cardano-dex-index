package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.core.models.models.BlockHash

final case class ExampleData(  blockHash: BlockHash,
                               blockIndex: Long,
                               globalIndex: Long)
