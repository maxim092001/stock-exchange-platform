package org.maximgran.stock_exchange_platform
package auth

import java.util.Base64
import javax.crypto.spec.{ IvParameterSpec, PBEKeySpec, SecretKeySpec }
import javax.crypto.{ Cipher, SecretKeyFactory }
import config.types.{ IvAES, PasswordSalt }
import domain.auth._

import cats.effect.Sync
import cats.syntax.all._
import eu.timepit.refined.auto._


trait Crypto {
  def encrypt(value: Password): EncryptedPassword
  def decrypt(value: EncryptedPassword): Password
}

object Crypto {

  private def hexStringToByteArray(s: String): Array[Byte] = {
    val len  = s.length
    val data = new Array[Byte](len / 2)
    var i    = 0
    while ({
      i < len
    }) {
      data(i / 2) = ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16)).toByte

      i += 2
    }
    data
  }

  def make[F[_]: Sync](passwordSalt: PasswordSalt, ivAES: IvAES): F[Crypto] =
    Sync[F]
      .delay {
        val ivBytes  = hexStringToByteArray(ivAES.value.value)
        val iv       = new IvParameterSpec(ivBytes)
        val salt     = passwordSalt.secret.value.getBytes("UTF-8")
        val keySpec  = new PBEKeySpec("password".toCharArray, salt, 65536, 256)
        val factory  = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val bytes    = factory.generateSecret(keySpec).getEncoded
        val sKeySpec = new SecretKeySpec(bytes, "AES")
        val eCipher  = EncryptCipher(Cipher.getInstance("AES/CBC/PKCS5Padding"))
        eCipher.value.init(Cipher.ENCRYPT_MODE, sKeySpec, iv)
        val dCipher = DecryptCipher(Cipher.getInstance("AES/CBC/PKCS5Padding"))
        dCipher.value.init(Cipher.DECRYPT_MODE, sKeySpec, iv)
        (eCipher, dCipher)
      }
      .map { case (ec, dc) =>
        new Crypto {
          def encrypt(password: Password): EncryptedPassword = {
            val base64 = Base64.getEncoder
            val bytes  = password.value.getBytes("UTF-8")
            val result = new String(base64.encode(ec.value.doFinal(bytes)), "UTF-8")
            EncryptedPassword(result)
          }

          def decrypt(password: EncryptedPassword): Password = {
            val base64 = Base64.getDecoder
            val bytes  = base64.decode(password.value.getBytes("UTF-8"))
            val result = new String(dc.value.doFinal(bytes), "UTF-8")
            Password(result)
          }
        }
      }

}
