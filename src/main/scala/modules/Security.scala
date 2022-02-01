package org.maximgran.stock_exchange_platform
package modules

import auth._
import config.types._
import domain.auth._
import http.auth.users._
import services._

import cats.ApplicativeThrow
import cats.effect._
import cats.syntax.all._
import dev.profunktor.auth.jwt._
import dev.profunktor.redis4cats.RedisCommands
import eu.timepit.refined.auto._
import io.circe.parser.{ decode => jsonDecode }
import pdi.jwt._
import skunk.Session

object Security {
  def make[F[_]: Sync](
      cfg: AppConfig,
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String]
  ): F[Security[F]] = {

    val userJwtAuth: UserJwtAuth =
      UserJwtAuth(
        JwtAuth
          .hmac(
            cfg.tokenConfig.value.secret,
            JwtAlgorithm.HS256
          )
      )

    for {
      tokens <- JwtExpire.make[F].map(Tokens.make[F](_, cfg.tokenConfig.value, cfg.tokenExpiration))
      crypto <- Crypto.make[F](cfg.passwordSalt.value)
      users     = Users.make[F](postgres)
      auth      = Auth.make[F](cfg.tokenExpiration, tokens, users, redis, crypto)
      usersAuth = UsersAuth.common[F](redis)
    } yield new Security[F](auth, usersAuth, userJwtAuth) {}
  }
}

sealed abstract class Security[F[_]] private (
    val auth: Auth[F],
    val usersAuth: UsersAuth[F, CommonUser],
    val userJwtAuth: UserJwtAuth
)
