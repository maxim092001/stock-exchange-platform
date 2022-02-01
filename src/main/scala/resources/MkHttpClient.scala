package org.maximgran.stock_exchange_platform
package resources

import config.types.HttpClientConfig

import cats.effect.kernel.{ Async, Resource }
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

trait MkHttpClient[F[_]] {
  def newEmber(c: HttpClientConfig): Resource[F, Client[F]]
}

object MkHttpClient {
  def apply[F[_]: MkHttpClient]: MkHttpClient[F] = implicitly

  implicit def forAsync[F[_]: Async]: MkHttpClient[F] =
    (c: HttpClientConfig) =>
      EmberClientBuilder
        .default[F]
        .withTimeout(c.timeout)
        .withIdleTimeInPool(c.idleTimeInPool)
        .build
}
