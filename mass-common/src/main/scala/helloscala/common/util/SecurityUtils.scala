/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.util

import java.util

case class ByteSaltPassword(salt: Array[Byte], saltPassword: Array[Byte])

case class SaltPassword(salt: String, saltPassword: String) {
  require(
    StringUtils.isNoneBlank(salt) && salt.length == SaltPassword.SALT_LENGTH,
    s"salt字符串长度必需为${SaltPassword.SALT_LENGTH}")
  require(
    StringUtils.isNoneBlank(saltPassword) && saltPassword.length == SaltPassword.SALT_PASSWORD_LENGTH,
    s"salt字符串长度必需为${SaltPassword.SALT_PASSWORD_LENGTH}")
}

object SaltPassword {
  val SALT_LENGTH = 12
  val SALT_PASSWORD_LENGTH = 64
}

object SecurityUtils {
  final val CLIENT_KEY_LENGTH = 32
  final val ENCODING_AES_KEY_LENGTH = 43

  //  def generateBizMsgCrypt(configuration: Configuration): HSBizMsgCrypt = {
  //    val key = configuration.getString("helloscala.crypt.client-key")
  //    val encodingAesKey = configuration.getString("helloscala.crypt.encoding-aes-key")
  //    val appId = configuration.getString("helloscala.crypt.client-id")
  //    new HSBizMsgCrypt(key, encodingAesKey, appId)
  //  }
  //
  //  def generateBizMsgCrypt(key: String, encodingAesKey: String, clientId: String): HSBizMsgCrypt =
  //    new HSBizMsgCrypt(key, encodingAesKey, clientId)

  /**
   * 生成通用 Salt 及 Salt Password
   *
   * @param password 待生成密码
   * @return
   */
  def byteGeneratePassword(password: String): ByteSaltPassword = {
    val salt = Utils.randomBytes(SaltPassword.SALT_LENGTH)
    val saltPwd = DigestUtils.sha256(salt ++ password.getBytes)
    ByteSaltPassword(salt, saltPwd)
  }

  def generatePassword(password: String): SaltPassword = {
    val salt = Utils.randomString(SaltPassword.SALT_LENGTH)
    val saltPwd = DigestUtils.sha256Hex(salt ++ password)
    SaltPassword(salt, saltPwd)
  }

  /**
   * 校验密码
   *
   * @param salt     salt
   * @param saltPwd  salt password
   * @param password request password
   * @return
   */
  def matchSaltPassword(salt: Array[Byte], saltPwd: Array[Byte], password: Array[Byte]): Boolean = {
    val bytes = DigestUtils.sha256(salt ++ password)
    util.Arrays.equals(saltPwd, bytes)
  }

  def matchSaltPassword(salt: String, saltPwd: String, password: String): Boolean = {
    val securityPassword = DigestUtils.sha256Hex(salt + password)
    securityPassword == saltPwd
  }

}
