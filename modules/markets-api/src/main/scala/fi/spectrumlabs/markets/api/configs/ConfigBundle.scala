package fi.spectrumlabs.markets.api.configs

import derevo.derive
import derevo.pureconfig.pureconfigReader
import fi.spectrumlabs.core.config.ConfigBundleCompanion
import fi.spectrumlabs.core.http.cache.HttpCacheConfig
import fi.spectrumlabs.core.pg.PgConfig
import fi.spectrumlabs.core.redis.RedisConfig
import fi.spectrumlabs.rates.resolver.config.{NetworkConfig, TokenFetcherConfig}
import tofu.WithContext
import tofu.logging.derivation.loggable
import tofu.optics.macros.ClassyOptics

import scala.concurrent.duration.FiniteDuration

@ClassyOptics
@derive(loggable, pureconfigReader)
final case class ConfigBundle(
  marketsApi: MarketsApiConfig,
  ratesRedis: RedisConfig,
  httpRedis: RedisConfig,
  pg: PgConfig,
  http: HttpConfig,
  network: NetworkConfig,
  tokenFetcher: TokenFetcherConfig,
  httpCache: HttpCacheConfig,
  tf: TokenFetcherConfig1,
  cacheTtl: FiniteDuration,
  graphiteSettings: GraphiteSettings
)

object ConfigBundle extends WithContext.Companion[ConfigBundle] with ConfigBundleCompanion[ConfigBundle]
