package kafkasample.demo

import java.util.{Collections, Properties}
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.consumer.KafkaConsumer

object Consumer {
  @volatile private var isStop = false

  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "CountryCounter")
    val consumer = new KafkaConsumer[String, String](props)
    val thread = new Thread() {
      override def run(): Unit = Consumer.run(consumer)
    }
    try {
      thread.start()
    } finally {
      TimeUnit.SECONDS.sleep(50)
      isStop = true
      thread.join()
      consumer.close()
    }
  }

  private def run(consumer: KafkaConsumer[String, String]): Unit = {
    consumer.subscribe(Collections.singleton("customerCountries"))
    while (!isStop && !Thread.currentThread().isInterrupted) {
      val records = consumer.poll(100)
      records.forEach { record =>
        println(s"topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key: ${record.key()}, value = ${record.value()}")
      }
      consumer.commitAsync()
    }
  }

}
