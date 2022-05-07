package fi.spectrumlabs.streaming

final case class Record[K, V](key: K, value: V)
