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

import domain._
import domain.userstocks._
import ext.http4s.refined._
import domain.auth._
import services.Auth
import services.UserStocks

final case class UserStocksSecuredRoutes[F[_]: JsonDecoder: MonadThrow](
    userStocks: UserStocks[F]
) extends Http4sDsl[F] {
  private[routes] val prefixPath = "/user_stocks_secured"

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of { case ar @ POST -> Root / "buy" as user =>
    ar.req.decodeR[BuyStock] { buyStock =>
      userStocks.create(CreateUserStock(UserStockId(user.value.id, buyStock.ticker))) *> Created()
    }
  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )
}
