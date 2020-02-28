//package mass.model.job
//
//import helloscala.common.util.{ EnumTrait, EnumTraitCompanion }
//
//sealed abstract class RunStatus(override val companion: EnumTraitCompanion, override protected val value: Int)
//    extends EnumTrait
//
//object RunStatus extends EnumTraitCompanion {
//  self =>
//  override type Value = RunStatus
//
//  case object JOB_NORMAL extends RunStatus(self, 0)
//  case object JOB_ENABLE extends RunStatus(self, 1)
//  case object JOB_RUNNING extends RunStatus(self, 100)
//  case object JOB_OK extends RunStatus(self, 200)
//  case object JOB_FAILURE extends RunStatus(self, 500)
//}
