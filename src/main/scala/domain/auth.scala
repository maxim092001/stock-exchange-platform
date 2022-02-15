package org.maximgran.stock_exchange_platform
package domain

import optics.uuid

import java.util.UUID
import javax.crypto.Cipher

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe._
import io.circe.refined._
import io.estatico.newtype.macros.newtype

import scala.util.control.NoStackTrace

object auth {

  @derive(decoder, encoder, uuid, eqv, show)
  @newtype
  case class UserId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype
  case class UserName(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype
  case class Password(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype
  case class EncryptedPassword(value: String)

  @newtype
  case class EncryptCipher(value: Cipher)

  @newtype
  case class DecryptCipher(value: Cipher)

  // User registration

  @derive(encoder, decoder)
  @newtype
  case class UserNameParam(value: NonEmptyString) {
    def toDomain: UserName = UserName(value.toLowerCase)
  }

  @derive(encoder, decoder)
  @newtype
  case class PasswordParam(value: NonEmptyString) {
    def toDomain: Password = Password(value)
  }

  @derive(encoder, decoder)
  case class CreateUser(
      username: UserNameParam,
      password: PasswordParam
  )

  case class UserNotFound(username: UserName)    extends NoStackTrace
  case class UserNameInUse(username: UserName)   extends NoStackTrace
  case class InvalidPassword(username: UserName) extends NoStackTrace
  case object UnsupportedOperation               extends NoStackTrace

  // User login
  @derive(decoder, encoder)
  case class LoginUser(
      username: UserNameParam,
      password: PasswordParam
  )

  // User buy stock

  @derive(decoder, encoder)
  @newtype
  case class StockTickerParam(value: String)

  @derive(decoder, encoder)
  case class UserBuyStock(
      username: UserNameParam,
      stockTicker: StockTickerParam
  )
}
