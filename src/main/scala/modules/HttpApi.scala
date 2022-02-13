package org.maximgran.stock_exchange_platform
package modules

import http.routes._
import http.routes.secured._

import cats.effect.Async
import cats.syntax.all._
import dev.profunktor.auth.JwtAuthMiddleware
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._
import http.auth.users.CommonUser
import http.routes.auth.{ LoginRoutes, LogoutRoutes }

import scala.concurrent.duration.DurationInt

object HttpApi {
  def make[F[_]: Async](services: Services[F], security: Security[F]): HttpApi[F] =
    new HttpApi[F](services, security) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
    services: Services[F],
    security: Security[F]
) {
  private val usersMiddleware =
    JwtAuthMiddleware[F, CommonUser](security.userJwtAuth.value, security.usersAuth.findUser)

  // Auth routes
  private val loginRoutes             = LoginRoutes[F](security.auth).routes
  private val logoutRoutes            = LogoutRoutes[F](security.auth).routes(usersMiddleware)
  private val userRoutes              = UserRoutes[F](security.auth).routes
  private val userStocksSecuredRoutes = UserStocksSecuredRoutes[F](services.userStocks).routes(usersMiddleware)

  private val userStocksRoutes = UserStockRoutes[F](services.userStocks).routes
  private val stockRoutes      = StockRoutes[F](services.stocks).routes

  private val openRoutes: HttpRoutes[F] =
    stockRoutes <+> loginRoutes <+> logoutRoutes <+> userRoutes <+> userStocksRoutes <+> userStocksSecuredRoutes

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60.seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  private val routes = Router(
    "" -> openRoutes
  )

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)

}
