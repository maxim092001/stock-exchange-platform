package org.maximgran.stock_exchange_platform
package auth

import config.types._
import effects.JwtClock

import cats.effect.Sync
import cats.syntax.all._
import pdi.jwt.JwtClaim

trait JwtExpire[F[_]] {
  def expiresIn(claim: JwtClaim, exp: TokenExpiration): F[JwtClaim]
}

object JwtExpire {

  def make[F[_]: Sync]: F[JwtExpire[F]] = {
    JwtClock[F].utc.map { implicit jClock => (claim: JwtClaim, exp: TokenExpiration) =>
      Sync[F].delay(claim.issuedNow.expiresIn(exp.value.toMillis))
    }
  }

}
