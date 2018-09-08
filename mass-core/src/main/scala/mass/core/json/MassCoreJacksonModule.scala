package mass.core.json

import com.fasterxml.jackson.module.scala.JacksonModule

class MassCoreJacksonModule extends JacksonModule with ScalaPBJacksonModule {
  override def getModuleName: String = getClass.getSimpleName
}
