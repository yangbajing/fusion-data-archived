package mass.core

import mass.model.CommonStatus
import mass.model.job.{Program, TriggerType}
import scalapb.{GeneratedEnum, GeneratedEnumCompanion, GeneratedMessageCompanion}

object ScalaPBProtos {
  lazy val messagesCompanions: Seq[GeneratedMessageCompanion[_]] =
    mass.message.job.JobProto.messagesCompanions ++ mass.model.job.JobProto.messagesCompanions

  lazy val enumerationMessagesCompanions: List[GeneratedEnumCompanion[_ <: GeneratedEnum]] =
    CommonStatus ::
      TriggerType ::
      Program ::
      Nil

  lazy val enumerationCompanions: Map[String, GeneratedEnumCompanion[_ <: GeneratedEnum]] =
    enumerationMessagesCompanions.map { companion =>
      val name = companion.getClass.getName
      val companionName = name.take(name.indexOf('$'))
      companionName -> companion
    }.toMap
}
