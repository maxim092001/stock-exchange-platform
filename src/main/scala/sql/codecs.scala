package org.maximgran.stock_exchange_platform
package sql

import domain.stock._
import domain.auth._

import skunk._
import skunk.codec.all._

object codecs {

  // Stocks

  val stockId: Codec[StockId]         = uuid.imap[StockId](StockId(_))(_.value)
  val stockTicker: Codec[StockTicker] = varchar.imap[StockTicker](StockTicker(_))(_.value)
  val stockDescription: Codec[StockDescription] =
    varchar.imap[StockDescription](StockDescription(_))(_.value)

  // User
  val userId: Codec[UserId]     = uuid.imap[UserId](UserId(_))(_.value)
  val userName: Codec[UserName] = varchar.imap[UserName](UserName(_))(_.value)

  // Password
  val encPassword: Codec[EncryptedPassword] = varchar.imap[EncryptedPassword](EncryptedPassword(_))(_.value)
}
