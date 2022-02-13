package org.maximgran.stock_exchange_platform
package http.routes.secured

import cats.MonadThrow
import cats.syntax.all._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware
import org.http4s.server.Router

import http.auth.users._
import domain.userstocks._
import ext.http4s.refined._
import services.UserStocks

final case class UserStocksSecuredRoutes[F[_]: JsonDecoder: MonadThrow](
    userStocks: UserStocks[F]
) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/user_stocks_secured"

  object UserStocksTickerQueryParam extends OptionalQueryParamDecoderMatcher[UserStocksTickerParam]("ticker")

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of {
    case ar @ POST -> Root / "buy" as user =>
      ar.req.decodeR[BuyStock] { buyStock =>
        userStocks.create(CreateUserStock(UserStockId(user.value.id, buyStock.ticker))) *> Created()
      }
    // case GET -> Root as user =>
    //   Ok(userStocks.findByUserId(user.value.id))
    case GET -> Root :? UserStocksTickerQueryParam(ticker) as user =>
      ticker
        .map(t =>
          userStocks.findById(UserStockId(user.value.id, t.toDomain)).flatMap {
            case Some(x) => Ok(x)
            case None    => NotFound(s"No stock with such ticker: ${t.value}")
          }
        )
        .getOrElse(Ok(userStocks.findByUserId(user.value.id)))
  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )
}
