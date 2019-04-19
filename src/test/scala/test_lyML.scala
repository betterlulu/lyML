import com.ly.bi.lyML.lyMLRunner
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.sql.SparkSession

object test_lyML{

  def main(args: Array[String]):Unit={
    val spark = SparkSession.builder().master("local").appName("Spark decision tree classifier").config("spark.some.config.option", "some-value").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    val dataset = spark.sparkContext.textFile("file:///D:/TEST.csv").map(_.split(","))
    val runner = new lyMLRunner(2,1,"")
    runner.run(spark, dataset, dataset)

  }
}
