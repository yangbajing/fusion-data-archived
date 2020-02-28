package mass.core.module

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import javax.inject.{ Inject, Provider, Singleton }
import mass.db.slick.{ PgProfile, PgProfileExtension }

@Singleton
class PgProfileProvider @Inject() (system: ActorSystem) extends Provider[PgProfile] {
  override def get(): PgProfile = PgProfileExtension(system)
}

class MassCoreModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PgProfile]).toProvider(classOf[PgProfileProvider])
  }
}
