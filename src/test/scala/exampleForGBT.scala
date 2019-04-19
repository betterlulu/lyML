
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.classification.GBTClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{DataFrame, SparkSession}

object exampleForGBT {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("Spark decision tree classifier").config("spark.some.config.option", "some-value").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._
    val dataset = spark.sparkContext.textFile("file:///D:/TEST.csv").map(_.split(","))

    def xx(x: Array[String]): Array[Double] = {
      val k = new Array[Double](x.length - 1)
      for(i <- (1 to x.length - 1)){
        k(i - 1) = x(i).toDouble
      }
      return k
    }

    val LabeledPointRdd = dataset.map(x => LabeledPoint(x(0).toInt, Vectors.dense(xx(x))))

    val Array(trainingData, testData) = spark.createDataFrame(LabeledPointRdd).randomSplit(Array(0.7, 0.3))

    val dt = new GBTClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setImpurity("gini")
//      .setMaxBins(10)
      .setMaxDepth(5)
      .setMinInfoGain(0.01) //一个节点分裂的最小信息增益，值为[0,1]
      .setMinInstancesPerNode(100) //每个节点包含的最小样本数
      .setSeed(123456)

    // 将索引标签转换回原始标签
    //val labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)

    // Chain indexers and tree in a Pipeline.
    val pipeline = new Pipeline().setStages(Array(dt))

    // Train model. This also runs the indexers.
    val model = pipeline.fit(trainingData)

    // 作出预测
    val predictions = model.transform(testData)

    // 选择几个示例行展示
    // predictions.select("predictedLabel", "label", "features").show(5, truncate = false)
    println("guhualu mark !")
    predictions.show(100)
    // 选择（预测标签，实际标签），并计算测试误差。
    //val evaluator = new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("accuracy")
    val evaluator =new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderROC")
    val accuracy = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))
    val AUPR =new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderPR").evaluate(predictions)
    val AUC = new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderROC").evaluate(predictions)
    println(AUPR,AUC)
  }

}
