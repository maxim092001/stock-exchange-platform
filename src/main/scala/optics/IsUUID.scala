package org.maximgran.stock_exchange_platform
package optics

import java.util.UUID
import monocle.Iso
import org.maximgran.stock_exchange_platform.ext.derevo.Derive

trait IsUUID[A] {
  def _UUID: Iso[UUID, A]
}

object IsUUID {
  def apply[A: IsUUID]: IsUUID[A] = implicitly

  implicit val identityUUID: IsUUID[UUID] = new IsUUID[UUID] {
    val _UUID = Iso[UUID, UUID](identity)(identity)
  }
}

object uuid extends Derive[IsUUID]
