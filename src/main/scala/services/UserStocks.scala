package org.maximgran.stock_exchange_platform
package services

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

import domain.userstocks._
import domain.stock._
import sql.codecs._
import domain.auth._

trait UserStocks[F[_]] {
  def findByUserId(userId: UserId): F[List[UserStock]]
  def findById(id: UserStockId): F[Option[UserStock]]
  def findAll: F[List[UserStock]]
  def create(userStocks: CreateUserStock): F[UserStockId]
}

object UserStocks {
  def make[F[_]: Concurrent](
      postgres: Resource[F, Session[F]]
  ): UserStocks[F] =
    new UserStocks[F] {
      import UserStocksSQL._

      override def findByUserId(userId: UserId): F[List[UserStock]] = postgres.use { session =>
        session.prepare(selectByUserId).use { q =>
          q.stream(userId, 1024).compile.toList
        }
      }

      override def findAll: F[List[UserStock]] = postgres.use(_.execute(selectAll))

      override def findById(id: UserStockId): F[Option[UserStock]] = postgres.use { session =>
        session.prepare(selectById).use(_.option(id))
      }

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

  val selectById: Query[UserStockId, UserStock] =
    sql"""
         select * from user_stocks as us
         where us.user_id = $userId AND us.ticker = $stockTicker
         """.query(decoder).contramap { case UserStockId(id, t) => id ~ t }

  val selectByUserId: Query[UserId, UserStock] =
    sql"""
         select * from user_stocks
         where user_id = $userId
       """.query(decoder)

  val insertUserStock: Command[UserStock] =
    sql"""
        insert into user_stocks
        values ($codec)
       """.command
}
