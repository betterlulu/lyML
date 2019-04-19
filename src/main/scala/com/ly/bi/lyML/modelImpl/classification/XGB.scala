package com.ly.bi.lyML.modelImpl.classification

import com.ly.bi.lyML.model
import ml.dmlc.xgboost4j.scala.spark.XGBoost
import org.apache.spark.sql.DataFrame

class XGB(dataset: DataFrame,testdataset: DataFrame) extends model{

/*  val splits = dataset.randomSplit(Array(0.7, 0.3))
  val LRmodel = new LogisticRegression().setLabelCol("label").setFeaturesCol("features").fit(splits(0).cache())

  def predict(n:Int) =println(s"Coefficients: ${LRmodel.coefficients} Intercept: ${LRmodel.intercept}")
  val pred=LRmodel.transform(splits(1).cache())*/

  val xgbParam = Map(
    "eta" -> 0.1f,
    "max_depth" -> 5,
    "silent" -> 0,
    "objective" -> "binary:logistic",
    "num_round" -> 100,
    "eval_metric" -> "auc")
  val model = XGBoost.trainWithDataFrame(dataset, xgbParam, round = 100, nWorkers = 100)

  def predict(n:Int) =println(s"Coefficients: ${model.toString()} Intercept: ${model.toString()}")
  val predictions = model.transform(testdataset)

  //val xgbClassificationModel = xgbClassifier.fit()
  //val results = xgbClassificationModel.transform()
}
