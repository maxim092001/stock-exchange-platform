package org.maximgran.stock_exchange_platform
package domain

import ext.http4s.queryParam
import ext.http4s.refined._
import optics.uuid

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

object stock {

  @derive(show, decoder, encoder, uuid, eqv)
  @newtype
  case class StockId(value: UUID)

  @derive(show, decoder, encoder, eqv)
  @newtype
  case class StockToken(value: String)

  @derive(show, decoder, encoder, eqv)
  @newtype
  case class StockDescription(value: String)

  @derive(decoder, encoder, eqv, show)
  case class Stock(
      id: StockId,
      token: StockToken,
      description: StockDescription
  )

  // Create

  @derive(decoder, encoder, show)
  case class CreateStockParam(token: StockTokenParam, description: StockDescriptionParam) {
    def toDomain: CreateStock = CreateStock(StockToken(token.value), StockDescription(description.value))
  }

  case class CreateStock(
      token: StockToken,
      description: StockDescription
  )

  // Params

  @derive(decoder, encoder, show)
  @newtype
  case class StockDescriptionParam(value: NonEmptyString)

  @derive(decoder, encoder, show)
  @newtype
  case class StockTokenParam(value: NonEmptyString)

  @derive(queryParam, show)
  @newtype
  case class StockQueryTokenParam(
      value: NonEmptyString
  ) {
    def toDomain: StockToken = StockToken(value)
  }

  object StockQueryTokenParam {
    implicit val jsonEncoder: Encoder[StockQueryTokenParam] =
      Encoder.forProduct1("name")(_.value)

    implicit val jsonDecoder: Decoder[StockQueryTokenParam] =
      Decoder.forProduct1("name")(StockQueryTokenParam.apply)
  }

}
