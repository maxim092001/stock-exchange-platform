package org.maximgran.stock_exchange_platform
package config

import scala.concurrent.duration._

import ext.ciris._

import ciris._
import ciris.refined._
import com.comcast.ip4s.{ Host, Port }
import derevo.cats.show
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import org.http4s.Uri

object types {

  @derive(configDecoder, show)
  @newtype
  case class JwtSecretKeyConfig(secret: NonEmptyString)

  @derive(configDecoder, show)
  @newtype
  case class JwtAccessTokenKeyConfig(secret: NonEmptyString)

  @derive(configDecoder, show)
  @newtype
  case class JwtClaimConfig(secret: NonEmptyString)

  @derive(configDecoder, show)
  @newtype
  case class PasswordSalt(secret: NonEmptyString)

  @derive(configDecoder, show)
  @newtype
  case class IvAES(value: NonEmptyString)

  @newtype case class TokenExpiration(value: FiniteDuration)

  case class AppConfig(
      tokenConfig: Secret[JwtAccessTokenKeyConfig],
      passwordSalt: Secret[PasswordSalt],
      tokenExpiration: TokenExpiration,
      httpServerConfig: HttpServerConfig,
      httpClientConfig: HttpClientConfig,
      postgreSQLConfig: PostgreSQLConfig,
      redisConfig: RedisConfig,
      ivAES: IvAES
  )

  // http
  case class HttpServerConfig(
      host: Host,
      port: Port
  )

  case class HttpClientConfig(
      timeout: FiniteDuration,
      idleTimeInPool: FiniteDuration
  )

  // Postgres
  case class PostgreSQLConfig(
      host: NonEmptyString,
      port: UserPortNumber,
      user: NonEmptyString,
      password: Secret[NonEmptyString],
      database: NonEmptyString,
      max: PosInt
  )

  // Redis
  @newtype case class RedisURI(value: NonEmptyString)
  @newtype case class RedisConfig(uri: RedisURI)

  // Alphavantage
  @newtype case class AlphavantageApiKey(value: NonEmptyString)
  @newtype case class AlphavantageUri(value: Uri)
  case class AlphavantageConfig(
      apiKey: AlphavantageApiKey,
      uri: AlphavantageUri
  )
}
