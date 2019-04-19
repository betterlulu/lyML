package com.ly.bi.lyML.modelImpl.classification

import com.ly.bi.lyML.model
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassifier, GBTClassifier}
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, MulticlassClassificationEvaluator}
import org.apache.spark.ml.feature._
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.DataFrame

class GBT(dataset: DataFrame,testdataset: DataFrame) extends model{

  //val Array(trainingData, testData) = spark.createDataFrame(LabeledPointRdd).randomSplit(Array(0.7, 0.3))

  val gbt = new GBTClassifier()
    .setLabelCol("label")
    .setFeaturesCol("features")
    .setImpurity("gini")
    .setMaxDepth(5)
    .setMinInfoGain(0.08) //一个节点分裂的最小信息增益，值为[0,1]
    .setMinInstancesPerNode(100) //每个节点包含的最小样本数
    .setSeed(123)

  val pipeline = new Pipeline().setStages(Array(gbt))
  val model = pipeline.fit(dataset)

  def predict(n:Int) =println(s"Coefficients: ${model.stages} Intercept: ${model.params}")
  val predictions = model.transform(testdataset)

  val AUPR =new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderPR").evaluate(predictions)
  val AUC = new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderROC").evaluate(predictions)
  println(s"AUPR: $AUPR, AUC: $AUC")

}
