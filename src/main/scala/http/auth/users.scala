package org.maximgran.stock_exchange_platform
package http.auth

import domain.auth._

import derevo.cats.show
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import dev.profunktor.auth.jwt._
import io.estatico.newtype.macros.newtype

object users {

  @newtype
  case class UserJwtAuth(value: JwtSymmetricAuth)

  @derive(decoder, encoder, show)
  case class User(id: UserId, name: UserName)

  @derive(decoder, encoder, show)
  case class UserWithPassword(id: UserId, name: UserName, password: EncryptedPassword)

  @derive(show)
  @newtype
  case class CommonUser(value: User)
}
