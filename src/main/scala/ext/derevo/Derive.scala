package org.maximgran.stock_exchange_platform
package ext.derevo

import derevo.{ Derivation, NewTypeDerivation }

import scala.annotation.implicitNotFound

trait Derive[F[_]] extends Derivation[F] with NewTypeDerivation[F] {
  def instance(implicit ev: OnlyNewtypes): Nothing = ev.absurd

  @implicitNotFound("Only newtypes instances can be derived")
  abstract final class OnlyNewtypes {
    def absurd: Nothing = ???
  }
}
