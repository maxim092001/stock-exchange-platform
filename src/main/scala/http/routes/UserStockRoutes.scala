package org.maximgran.stock_exchange_platform
package http.routes

import domain._
import domain.auth._
import services.Auth
import services.UserStocks
import ext.http4s.refined._

import cats.syntax.all._
import cats.{ Applicative, MonadThrow }
import io.circe.JsonObject
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class UserStockRoutes[F[_]: JsonDecoder: MonadThrow](
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
