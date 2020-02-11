package mass.core.script

import javax.script.{ ScriptEngine, ScriptEngineManager }

object ScriptEngineType extends Enumeration {
  type ScriptEngineType = Value

  val ENGINE_SCALA = Value(1, "scala")
  val ENGINE_JAVASCRIPT = Value("nashorn")
}

object ScriptManager {
  import ScriptEngineType._

  val engineManager = new ScriptEngineManager()

  def scriptScala: ScriptEngine =
    engineManager.getEngineByName(ENGINE_SCALA.toString)

  def scriptJavascript: ScriptEngine =
    engineManager.getEngineByName(ENGINE_JAVASCRIPT.toString)
}
