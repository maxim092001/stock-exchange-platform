package org.maximgran.stock_exchange_platform
package modules

import effects.GenUUID
import services._

import cats.effect._
import skunk.Session
import dev.profunktor.redis4cats.RedisCommands

object Services {
  def make[F[_]: GenUUID: Temporal](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String]
  ): Services[F] =
    new Services[F](Stocks.make[F](postgres), UserStocks.make[F](postgres)) {}
}

sealed abstract class Services[F[_]] private (
    val stocks: Stocks[F],
    val userStocks: UserStocks[F]
)
