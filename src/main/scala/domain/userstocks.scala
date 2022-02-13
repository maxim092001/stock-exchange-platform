package org.maximgran.stock_exchange_platform
package domain

import ext.http4s.queryParam
import ext.http4s.refined._
import optics.uuid
import auth._
import stock._

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.circe.{ Decoder, Encoder }
import io.estatico.newtype.macros.newtype

import java.util.UUID

object userstocks {

  @derive(encoder, decoder, eqv, show)
  @newtype
  case class Quantity(value: Int)

  @derive(show, decoder, encoder, eqv)
  case class UserStockId(userId: UserId, stockTicker: StockTicker)

  @derive(decoder, encoder, eqv, show)
  case class UserStock(
      id: UserStockId
  )

  case class CreateUserStock(
      userStockId: UserStockId
  )

  @derive(eqv, show)
  case class BuyStock(ticker: StockTicker, quantity: Quantity)

  object BuyStock {
    implicit val jsonEncoder: Encoder[BuyStock] =
      Encoder.forProduct2("ticker", "quantity")(s => s.ticker -> s.quantity)

    implicit val jsonDecoder: Decoder[BuyStock] =
      Decoder.forProduct2("ticker", "quantity")((t, q) => BuyStock(t, q))
  }
}
