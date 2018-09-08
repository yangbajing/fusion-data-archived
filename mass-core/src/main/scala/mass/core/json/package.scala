package mass.core
import mass.model.CommonStatus
import mass.model.job.{Program, TriggerType}
import scalapb.GeneratedEnum

package object json {
  val ClassGeneratedEnum: Class[GeneratedEnum] = classOf[scalapb.GeneratedEnum]

  val ClassCommonStatus: Class[CommonStatus] = classOf[CommonStatus]
  val ClassTriggerType: Class[TriggerType] = classOf[TriggerType]
  val ClassProgram: Class[Program] = classOf[Program]
}
