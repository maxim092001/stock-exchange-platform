package org.maximgran.stock_exchange_platform
package services

import domain.auth._
import http.auth.users._
import domain.ID
import sql.codecs._
import effects.GenUUID

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

trait Users[F[_]] {
  def find(username: UserName): F[Option[UserWithPassword]]
  def create(username: UserName, password: EncryptedPassword): F[UserId]
}

object Users {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgresql: Resource[F, Session[F]]
  ) = new Users[F] {
    import UsersSQL._

    def find(username: UserName): F[Option[UserWithPassword]] =
      postgresql.use { session =>
        session.prepare(selectUser).use { q =>
          q.option(username).map {
            case Some(u ~ p) => UserWithPassword(u.id, u.name, p).some
            case _           => none[UserWithPassword]
          }
        }
      }

    def create(username: UserName, password: EncryptedPassword): F[UserId] =
      postgresql.use { session =>
        session.prepare(insertUser).use { cmd =>
          ID.make[F, UserId].flatMap { id =>
            cmd
              .execute(User(id, username) ~ password)
              .as(id)
              .recoverWith { case SqlState.UniqueViolation(_) =>
                UserNameInUse(username).raiseError[F, UserId]
              }
          }
        }
      }
  }

}

private object UsersSQL {

  val codec: Codec[User ~ EncryptedPassword] =
    (userId ~ userName ~ encPassword).imap { case i ~ n ~ p =>
      User(i, n) ~ p
    } { case u ~ p => u.id ~ u.name ~ p }

  val selectUser: Query[UserName, User ~ EncryptedPassword] =
    sql"""
        select * from users
        where name = $userName
       """.query(codec)

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""
        insert into users
        values ($codec)
       """.command
}
