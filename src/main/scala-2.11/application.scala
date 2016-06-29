import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import sinks.ElasticsearchUpsertSink

object application {
  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.disableClosureCleaner()
    // Elasticsearch parameters
    val writeToElasticsearch = false // set to true to write results to Elasticsearch
    val elasticsearchHost = "127.0.0.1" // look-up hostname in Elasticsearch log output
    val elasticsearchPort = 9300
    val socketStream = env.socketTextStream("localhost", 44444)

    val wordStream = socketStream
      .flatMap(value => value.toLowerCase().split("\\s+"))
      .map(value => (value, 1))

    val kvPair = wordStream.keyBy(0)

    val countPair = kvPair.sum(1)

    if (writeToElasticsearch)
      countPair.addSink(new WordCountUpsert(elasticsearchHost, elasticsearchPort))
    else
      countPair.print()
    env.execute()
  }

  class WordCountUpsert(host: String, port: Int)
    extends ElasticsearchUpsertSink[(String, Int)] (
      host,
      port,
      "elasticsearch",
      "word-count-demo",
      "word-counts") {
    override def insertJson(r: (String, Int)): Map[String, AnyRef] = {
      Map(
        "word" -> r._1.asInstanceOf[AnyRef],
        "cnt" -> r._2.asInstanceOf[AnyRef]
      )
    }

    override def updateJson(r: (String, Int)): Map[String, AnyRef] = {
      Map[String, AnyRef] (
        "cnt" -> r._2.asInstanceOf[AnyRef]
      )
    }

    override def indexKey(r: (String, Int)): String = {
      // index by location and time
      r._1.toString
    }

  }
}
