package mass.core.enum

object RunStatusEnum extends Enumeration {
  val JOB_NORMAL = Value(0, "普通")
  val JOB_ENABLE = Value(1, "启用")
  val JOB_RUNNING = Value(100, "运行")
  val JOB_OK = Value(200, "成功完成")
  val JOB_FAILURE = Value(500, "失败结束")

  lazy val toTitleIdValues = EnumUtils.enumToTitleIdValues(this)
}
