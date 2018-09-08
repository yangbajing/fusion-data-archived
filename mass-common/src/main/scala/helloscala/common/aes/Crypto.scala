/*
 * Copyright 2018 羊八井(yangbajing)（杨景）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helloscala.common.aes

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util
import java.util.Base64
import javax.crypto.spec.SecretKeySpec
import javax.crypto.{Cipher, Mac}

object Crypto {
  import SessionUtil._

  val CRYPTO_NAME = "AES"
  val KEY_SPEC_NAME = "AES"

  def signHmacSHA1Hex(message: String, secret: String): String = {
    val key = secret.getBytes("UTF-8")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(new SecretKeySpec(key, "HmacSHA1"))
    toHexString(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)))
  }

  def signHmacSHA256Base64(message: String, secret: String): String = {
    val key = secret.getBytes("UTF-8")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(new SecretKeySpec(key, "HmacSHA256"))
    Base64.getUrlEncoder
      .withoutPadding()
      .encodeToString(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)))
  }

  def encryptAES(value: String, secret: String): String = {
    val raw = util.Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 16)
    val skeySpec = new SecretKeySpec(raw, KEY_SPEC_NAME)
    val cipher = Cipher.getInstance(CRYPTO_NAME)
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
    toHexString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)))
  }

  def decryptAES(value: String, secret: String): String = {
    val raw = util.Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 16)
    val skeySpec = new SecretKeySpec(raw, KEY_SPEC_NAME)
    val cipher = Cipher.getInstance(CRYPTO_NAME)
    cipher.init(Cipher.DECRYPT_MODE, skeySpec)
    new String(cipher.doFinal(hexStringToByte(value)))
  }

  def hashSHA256(value: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    toHexString(digest.digest(value.getBytes(StandardCharsets.UTF_8)))
  }

}
