package mass.core

import akka.actor.testkit.typed.scaladsl.{ ActorTestKit, ScalaTestWithActorTestKit }
import com.typesafe.config.{ Config, ConfigFactory }
import fusion.common.config.FusionConfigFactory
import mass.Global
import org.scalatest.{ EitherValues, OptionValues }

abstract class MassActorTestKit(testKit: ActorTestKit)
    extends ScalaTestWithActorTestKit(testKit)
    with OptionValues
    with EitherValues {
  def this() = {
    this(
      ActorTestKit(
        Constants.MASS,
        FusionConfigFactory.arrangeConfig(ConfigFactory.load("application-test.conf"), Constants.MASS)))
  }

  def this(resourceBasename: String) = {
    this(
      ActorTestKit(
        Constants.MASS,
        FusionConfigFactory.arrangeConfig(ConfigFactory.load(resourceBasename), Constants.MASS)))
  }

  def this(customConfig: Config) = {
    this(ActorTestKit(Constants.MASS, customConfig))
  }

  Global.registerActorSystem(system)
}
