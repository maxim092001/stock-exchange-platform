package org.maximgran.stock_exchange_platform
package config

import enumeratum.EnumEntry._
import enumeratum._

sealed abstract class AppEnvironment extends EnumEntry with Lowercase

object AppEnvironment extends Enum[AppEnvironment] with CirisEnum[AppEnvironment] {
  case object Test extends AppEnvironment
  case object Prod extends AppEnvironment

  val values: IndexedSeq[AppEnvironment] = findValues
}
