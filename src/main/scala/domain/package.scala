package org.maximgran.stock_exchange_platform

package object domain extends OrphanInstances

import dev.profunktor.auth.jwt.JwtToken
import io.circe.{ Decoder, Encoder }
import cats.{ Eq, Monoid, Show }
import cats.syntax.contravariant._

trait OrphanInstances {

  implicit val tokenEq: Eq[JwtToken] = Eq.by(_.value)

  implicit val tokenShow: Show[JwtToken] = Show[String].contramap[JwtToken](_.value)

  implicit val tokenEncoder: Encoder[JwtToken] =
    Encoder.forProduct1("access_token")(_.value)
}
