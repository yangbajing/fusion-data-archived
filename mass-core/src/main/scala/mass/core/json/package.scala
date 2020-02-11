package mass.core
import mass.data.CommonStatus
import mass.data.job.{ Program, TriggerType }
import scalapb.GeneratedEnum

package object json {
  val ClassGeneratedEnum: Class[GeneratedEnum] = classOf[scalapb.GeneratedEnum]

  val ClassCommonStatus: Class[CommonStatus] = classOf[CommonStatus]
  val ClassTriggerType: Class[TriggerType] = classOf[TriggerType]
  val ClassProgram: Class[Program] = classOf[Program]
}
