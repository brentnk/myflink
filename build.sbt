name := "myflink"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.flink" % "flink-scala_2.11" % "1.0.3",
  "org.apache.flink" % "flink-clients_2.11" % "1.0.3",
  "org.apache.flink" % "flink-streaming-scala_2.11" % "1.0.3",
  "org.apache.flink" % "flink-connector-elasticsearch_2.11" % "1.0.3",
  "org.elasticsearch" % "elasticsearch" % "2.3.3"
)