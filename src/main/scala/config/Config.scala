package org.maximgran.stock_exchange_platform
package config

import config.AppEnvironment._
import types._

import cats.effect.Async
import cats.syntax.all._
import ciris._
import ciris.refined._
import com.comcast.ip4s._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString

import scala.concurrent.duration._

object Config {

  def load[F[_]: Async]: F[AppConfig] =
    env("SE_APP_ENV")
      .as[AppEnvironment]
      .flatMap {
        case Test => default[F]
        case Prod => default[F]
      }
      .load[F]

  private def default[F[_]]: ConfigValue[F, AppConfig] =
    (
      env("SE_POSTGRES_PASSWORD").as[NonEmptyString].secret,
      env("SE_ACCESS_TOKEN_SECRET_KEY").as[JwtAccessTokenKeyConfig].secret,
      env("SE_PASSWORD_SALT").as[PasswordSalt].secret,
      env("SE_IV_AES").as[IvAES]
    )
      .parMapN { (postgresPassword, tokenConfig, passwordSalt, ivAES) =>
        AppConfig(
          httpServerConfig = HttpServerConfig(
            host = host"127.0.0.1",
            port = port"8888"
          ),
          httpClientConfig = HttpClientConfig(
            timeout = 60.seconds,
            idleTimeInPool = 30.seconds
          ),
          postgreSQLConfig = PostgreSQLConfig(
            host = "167.99.248.97", // TODO env
            port = 5432,
            user = "postgres",
            password = postgresPassword,
            database = "postgres",
            max = 10
          ),
          tokenConfig = tokenConfig,
          passwordSalt = passwordSalt,
          tokenExpiration = TokenExpiration(30.minutes),
          redisConfig = RedisConfig(
            RedisURI("redis://167.99.248.97:6379")
          ),
          ivAES = ivAES
        )

      }

}
