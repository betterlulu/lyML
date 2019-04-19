
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.IndexToString
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.sql.DataFrame

object exampleForDT {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("Spark decision tree classifier").config("spark.some.config.option", "some-value").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._

    val dataList: List[(Double, String, Double, Double, String, Double, Double, Double, Double)] = List(
      (0, "male", 37, 10, "no", 3, 18, 7, 4),
      (0, "female", 27, 4, "no", 4, 14, 6, 4),
      (1, "female", 32, 15, "yes", 1, 12, 1, 4),
      (1, "male", 57, 15, "yes", 5, 18, 6, 5),
      (0, "male", 22, 0.75, "no", 2, 17, 6, 3),
      (1, "female", 32, 1.5, "no", 2, 17, 5, 5))

    val data = dataList.toDF("affairs", "gender", "age", "yearsmarried", "children", "religiousness", "education", "occupation", "rating")

    data.createOrReplaceTempView("data")

    // 字符类型转换成数值
    val labelWhere = "case when affairs=0 then 0 else cast(1 as double) end as label"
    val genderWhere = "case when gender='female' then 0 else cast(1 as double) end as gender"
    val childrenWhere = "case when children='no' then 0 else cast(1 as double) end as children"

    val dataLabelDF = spark.sql(s"select $labelWhere, $genderWhere,age,yearsmarried,$childrenWhere,religiousness,education,occupation,rating from data")

    val featuresArray = Array("gender", "age", "yearsmarried", "children", "religiousness", "education", "occupation", "rating")

    // 字段转换成特征向量
    val assembler = new VectorAssembler().setInputCols(featuresArray).setOutputCol("features")
    val vecDF: DataFrame = assembler.transform(dataLabelDF)
    vecDF.show(10, truncate = false)

    // 索引标签，将元数据添加到标签列中
    val labelIndexer = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(vecDF)
    labelIndexer.transform(vecDF).show(10, truncate = false)

    // 自动识别分类的特征，并对它们进行索引
    // 具有大于5个不同的值的特征被视为连续。
    val featureIndexer = new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(5).fit(vecDF)
    featureIndexer.transform(vecDF).show(10, truncate = false)

    // 将数据分为训练和测试集（30%进行测试）
    val Array(trainingData, testData) = vecDF.randomSplit(Array(0.7, 0.3))


    val dt = new DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setImpurity("entropy")
//      .setMaxBins(10)
      .setMaxDepth(5)
      .setMinInfoGain(0.01) //一个节点分裂的最小信息增益，值为[0,1]
      .setMinInstancesPerNode(100) //每个节点包含的最小样本数
      .setSeed(123456)

    // 将索引标签转换回原始标签
    val labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)

    // Chain indexers and tree in a Pipeline.
    val pipeline = new Pipeline().setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))

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
    val evaluator =new BinaryClassificationEvaluator().setLabelCol("indexedLabel").setRawPredictionCol("prediction").setMetricName("areaUnderROC")
    val accuracy = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))
  }

}
