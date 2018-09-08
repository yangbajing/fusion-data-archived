package mass.session

trait BaseSession {
  def openId: String

  // 接入的应用程序
  def appId: String

  // 接入应用程序自己的用户ID
  def userId: String

  def sessionCode: String

  def expiresIn: Long

  /**
   * 是否已过期
   * @return
   */
  def isDue: Boolean = expiresIn < (System.currentTimeMillis() / 1000)
}
