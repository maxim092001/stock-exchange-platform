package org.maximgran.stock_exchange_platform
package services

import domain.ID
import domain.stock.{ CreateStock, Stock, StockId, StockToken }
import effects.GenUUID
import sql.codecs._

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

trait Stocks[F[_]] {
  def findById(id: StockId): F[Option[Stock]]
  def findAll: F[List[Stock]]
  def findBy(token: StockToken): F[Option[Stock]]
  def create(stock: CreateStock): F[StockId]
}

object Stocks {
  def make[F[_]: Concurrent: GenUUID](
      postgres: Resource[F, Session[F]]
  ): Stocks[F] =
    new Stocks[F] {

      import StockSQL._

      def findById(stockId: StockId): F[Option[Stock]] =
        postgres.use { session =>
          session.prepare(selectById).use { ps =>
            ps.option(stockId)
          }
        }
      def findAll: F[List[Stock]] = postgres.use(_.execute(selectAll))

      def findBy(token: StockToken): F[Option[Stock]] = postgres.use { session =>
        session.prepare(selectByToken).use { ps =>
          ps.option(token)
        }
      }

      def create(stock: CreateStock): F[StockId] = postgres.use { session =>
        session.prepare(insertStock).use { cmd =>
          ID.make[F, StockId].flatMap { id =>
            cmd.execute(Stock(id, stock.token, stock.description)).as(id)
          }
        }
      }
    }
}

private object StockSQL {

  val codec: Codec[Stock] =
    (stockId ~ stockToken ~ stockDescription).imap { case id ~ t ~ d =>
      Stock(id, t, d)
    }(s => s.id ~ s.token ~ s.description)

  val decoder: Decoder[Stock] =
    (stockId ~ stockToken ~ stockDescription).map { case id ~ t ~ d =>
      Stock(id, t, d)
    }

  val selectAll: Query[Void, Stock] =
    sql"""
         select * from stocks
       """.query(decoder)

  val selectById: Query[StockId, Stock] =
    sql"""
         select i.uuid, i.token, i.description
         from stocks as i
         where i.uuid = $stockId
       """.query(decoder)

  val selectByToken: Query[StockToken, Stock] =
    sql"""
        select i.uuid, i.token, i.description
        from stocks as i
        where i.token LIKE $stockToken
       """.query(decoder)

  val insertStock: Command[Stock] =
    sql"""
        insert into stocks
        values ($codec)
       """.command
}
