package org.maximgran.stock_exchange_platform
package services

import domain.ID
import domain.stock.{ CreateStock, Stock, StockId, StockTicker }
import effects.GenUUID
import sql.codecs._

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

trait Stocks[F[_]] {
  def findById(id: StockId): F[Option[Stock]]
  def findAll: F[List[Stock]]
  def findBy(ticker: StockTicker): F[Option[Stock]]
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

      def findBy(token: StockTicker): F[Option[Stock]] = postgres.use { session =>
        session.prepare(selectByTicker).use { ps =>
          ps.option(token)
        }
      }

      def create(stock: CreateStock): F[StockId] = postgres.use { session =>
        session.prepare(insertStock).use { cmd =>
          ID.make[F, StockId].flatMap { id =>
            cmd.execute(Stock(id, stock.ticker, stock.description)).as(id)
          }
        }
      }
    }
}

private object StockSQL {

  val codec: Codec[Stock] =
    (stockId ~ stockTicker ~ stockDescription).imap { case id ~ t ~ d =>
      Stock(id, t, d)
    }(s => s.id ~ s.ticker ~ s.description)

  val decoder: Decoder[Stock] =
    (stockId ~ stockTicker ~ stockDescription).map { case id ~ t ~ d =>
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

  val selectByTicker: Query[StockTicker, Stock] =
    sql"""
        select i.uuid, i.token, i.description
        from stocks as i
        where i.token LIKE $stockTicker
       """.query(decoder)

  val insertStock: Command[Stock] =
    sql"""
        insert into stocks
        values ($codec)
       """.command
}
