package org.maximgran.stock_exchange_platform
package http.routes

import cats.MonadThrow
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import services.UserStocks

final case class UserStockRoutes[F[_]: MonadThrow](
    userStocks: UserStocks[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "user_stocks"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root =>
    Ok(userStocks.findAll)
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
