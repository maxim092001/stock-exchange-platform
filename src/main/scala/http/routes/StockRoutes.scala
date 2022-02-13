package org.maximgran.stock_exchange_platform
package http.routes

import domain.stock.{ CreateStockParam, Stock, StockQueryTickerParam, StockTickerParam }
import ext.http4s.refined.RefinedRequestDecoder
import services.Stocks

import cats.syntax.all._
import cats.{ Applicative, MonadThrow }
import io.circe.JsonObject
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class StockRoutes[F[_]: JsonDecoder: MonadThrow](
    stocks: Stocks[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/stocks"

  object StockTickerQueryParam extends OptionalQueryParamDecoderMatcher[StockQueryTickerParam]("ticker")

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? StockTickerQueryParam(ticker) =>
      ticker
        .map(t =>
          stocks.findBy(t.toDomain).flatMap {
            case Some(x) => Ok(x)
            case None    => NotFound(s"No stock with such token: ${t.value}")
          }
        )
        .getOrElse(Ok(stocks.findAll))
    case r @ POST -> Root =>
      r.decodeR[CreateStockParam] { st =>
        stocks.create(st.toDomain).flatMap { id =>
          Created(JsonObject.singleton("uuid", id.asJson))
        }
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
