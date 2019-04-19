name := "lyML"

version := "0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "tongcheng public repository" at "http://nexus.17usoft.com/repository/mvn-all/",
  "tongcheng public repository2" at "http://nexus.17usoft.com/repository/mvn-all/",
  "spray repo" at "http://repo.spray.io/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "cloudera repo" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "conjars.org" at "http://conjars.org/repo",
  "Maven Central" at "http://repo1.maven.org/maven2/",
  "mvnrepository" at "http://mvnrepository.com/artifact/"
)

libraryDependencies ++= {
  val sparkVersion = "2.1.0"
  val jmhVersion = "1.19"
  Seq(
    "org.apache.spark" % "spark-core_2.11"% sparkVersion % "provided",
    "org.apache.spark" % "spark-sql_2.11"% sparkVersion % "provided",
    "org.apache.spark" % "spark-mllib_2.11"% sparkVersion % "provided",
    "org.apache.spark" % "spark-graphx_2.11"% sparkVersion % "provided",
    "com.github.scopt" %% "scopt" % "3.3.0",
    "ml.dmlc" % "xgboost4j-spark" % "0.72"
  )
}
////addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.8")

