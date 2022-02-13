package org.maximgran.stock_exchange_platform
package services

import domain.ID
import domain.stock.{ CreateStock, Stock, StockId, StockTicker }
import domain.userstocks._
import effects.GenUUID
import sql.codecs._
import domain.auth._

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

trait UserStocks[F[_]] {
  def findById(id: UserStockId): F[Option[UserStock]]
  def findAll: F[List[UserStock]]
  def create(userStocks: CreateUserStock): F[UserStockId]
}

object UserStocks {
  def make[F[_]: Concurrent: GenUUID](
      postgres: Resource[F, Session[F]]
  ): UserStocks[F] =
    new UserStocks[F] {
      import UserStocksSQL._

      override def findAll: F[List[UserStock]] = postgres.use(_.execute(selectAll))

      override def findById(id: UserStockId): F[Option[UserStock]] = ???

      override def create(userStocks: CreateUserStock): F[UserStockId] = postgres.use { session =>
        session.prepare(insertUserStock).use { cmd =>
          cmd.execute(UserStock(userStocks.userStockId)).as(userStocks.userStockId)
        }
      }

    }
}

object UserStocksSQL {

  val codec: Codec[UserStock] =
    (userId ~ stockTicker).imap { case id ~ t => UserStock(UserStockId(id, t)) }(us => us.id.userId ~ us.id.stockTicker)

  val decoder: Decoder[UserStock] =
    (userId ~ stockTicker).map { case id ~ t => UserStock(UserStockId(id, t)) }

  val selectAll: Query[Void, UserStock] =
    sql"""
         select * from user_stocks
       """.query(decoder)

  val selectById: Query[UserId ~ StockTicker, UserStock] =
    sql"""
         select us.userId, us.ticker
         from user_stocks as us
         where us.userId = $userId AND us.ticker LIKE $stockTicker
       """.query(decoder)

  val insertUserStock: Command[UserStock] =
    sql"""
        insert into user_stocks
        values ($codec)
       """.command
}
