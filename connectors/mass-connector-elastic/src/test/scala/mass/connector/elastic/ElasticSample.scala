package mass.connector.elastic

import akka.actor.ActorSystem
import akka.stream.alpakka.elasticsearch.scaladsl.{ ElasticsearchSink, ElasticsearchSource }
import akka.stream.alpakka.elasticsearch.{ ElasticsearchSourceSettings, ElasticsearchWriteSettings, WriteMessage }
import akka.stream.scaladsl.Sink
import akka.{ Done, NotUsed }
import org.apache.http.HttpHost
import org.apache.http.auth.{ AuthScope, UsernamePasswordCredentials }
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClient
import spray.json.{ JsObject, JsValue, JsonWriter }

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

object ElasticSample extends App {
  implicit val system = ActorSystem()
  implicit val writer = new JsonWriter[JsObject] {
    override def write(obj: JsObject): JsValue = obj
  }
  val credentialProvider = new BasicCredentialsProvider()
  credentialProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "M92nrZ9OHkxgHVMAaYpg"))
  val client: RestClient = RestClient
    .builder(new HttpHost("10.0.0.9", 9200))
    .setHttpClientConfigCallback((httpClientBuilder: HttpAsyncClientBuilder) =>
      httpClientBuilder.setDefaultCredentialsProvider(credentialProvider))
    .build()

  implicit val localClient = RestClient.builder(new HttpHost("localhost", 9200)).build()

  val settings = ElasticsearchSourceSettings().withBufferSize(200)

  val source =
    ElasticsearchSource("index_hongka_school_it", None, Map.empty[String, String], settings)(client).map { result =>
      WriteMessage.createCreateMessage(result.id, result.source)
    }

//  val f = source.take(10).runForeach(println)

  val sink: Sink[WriteMessage[JsObject, NotUsed], Future[Done]] =
    ElasticsearchSink.create[JsObject]("index_hongka_school", "_doc", ElasticsearchWriteSettings().withBufferSize(200))

  val f = source.runWith(sink)

  val r = Await.result(f, Duration.Inf)
  println("r: " + r)

  system.terminate()
  client.close()
  localClient.close()
}
