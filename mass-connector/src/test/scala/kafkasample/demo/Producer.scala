package kafkasample.demo

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

object Producer {

  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    try {
      run(producer)
    } finally {
      TimeUnit.SECONDS.sleep(5)
      producer.close()
    }
  }

  private def run[K, V](producer: KafkaProducer[String, String]) {
    val record = new ProducerRecord[String, String]("customerCountries", "羊八井222")
    producer.send(record, (metadata: RecordMetadata, e: Exception) => {
      if (e ne null) {
        e.printStackTrace()
      }
      println(s"metadata: $metadata")
    })
  }

}
