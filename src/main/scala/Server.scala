package org.maximgran.stock_exchange_platform

import config.types._
import config.Config
import modules._
import resources._

import cats.effect._
import cats.effect.std.Supervisor
import dev.profunktor.redis4cats.log4cats._
import eu.timepit.refined.auto._
import org.maximgran.stock_exchange_platform.domain.auth.{ CreateUser, PasswordParam, UserNameParam }
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.circe.syntax._

object Server extends IOApp.Simple {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    Config.load[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config $cfg") >> Supervisor[IO].use { implicit sp =>
        {
          AppResources
            .make[IO](cfg)
            .evalMap { res =>
              {
                Security.make[IO](cfg, res.postgres, res.redis).map { security =>
                  val services = Services.make[IO](res.postgres, res.redis)
                  val api      = HttpApi.make[IO](services, security)
                  cfg.httpServerConfig -> api.httpApp
                }
              }
            }
            .flatMap { case (cfg, httpApp) =>
              MkHttpServer[IO].newEmber(cfg, httpApp)
            }
            .useForever
        }
      }
    }
  }
}
