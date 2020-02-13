package mass.core.script

import fusion.test.FusionWordSpecLike
import javax.script.{ ScriptEngineManager, SimpleBindings }
import mass.core.event.EventData

class ScriptManagerTest extends FusionWordSpecLike {
  case class SqlDataEvent(data: String, `type`: String = "data-sql") extends EventData

  "ScriptManagerTest" should {
    "scriptJavascript" in {
      val engine = ScriptManager.scriptJavascript
      val event = SqlDataEvent("哈哈哈")
      val bindings = new SimpleBindings()
      bindings.put("event", event)
      val result = engine.eval("event.data()", bindings)
      println(s"result: ${result.getClass.getSimpleName} = $result")
    }

//    "scriptScala" in {
//      new ScriptEngineManager().getEngineFactories.forEach(println)
//
//      val engine = ScriptManager.scriptScala
//      println(s"engine: $engine")
//      val event = SqlDataEvent("哈哈哈")
//      val bindings = new SimpleBindings()
//      bindings.put("event", event)
//      val result = engine.eval("event.data", bindings)
//      println(s"result = $result")
//    }

    "set same" in {
      println(Set(1, 2, 3, 4).iterator.sameElements(Set(2, 3, 1, 4)))
      println(Set(1, 2, 3, 4) == Set(2, 3, 1, 4))
    }
  }
}
