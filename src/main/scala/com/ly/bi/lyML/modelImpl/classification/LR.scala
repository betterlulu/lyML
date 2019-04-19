package com.ly.bi.lyML.modelImpl.classification

import com.ly.bi.lyML.model
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator

class LR(dataset: DataFrame,testdataset: DataFrame) extends model{

/*  val tokenizer = new Tokenizer()
    .setInputCol("features")
    .setOutputCol("label")
  val hashingTF = new HashingTF()
    .setInputCol(tokenizer.getOutputCol)
    .setOutputCol("features")
  val lr = new LogisticRegression()
    .setMaxIter(10)
  val pipeline = new Pipeline()
    .setStages(Array(tokenizer, hashingTF, lr))

  val paramGrid = new ParamGridBuilder()
    .addGrid(hashingTF.numFeatures, Array(10, 100, 1000))
    .addGrid(lr.regParam, Array(0.1, 0.01))
    .build()

  val cv = new CrossValidator()
    .setEstimator(pipeline)
    .setEvaluator(new BinaryClassificationEvaluator)
    .setEstimatorParamMaps(paramGrid)
    .setNumFolds(10)

  // Run cross-validation, and choose the best set of parameters.
  val cvModel = cv.fit(dataset)*/


  val lr = new LogisticRegression().setLabelCol("label").setFeaturesCol("features")
  lr.setRegParam(2.0)
  lr.setMaxIter(200)

  val pipeline = new Pipeline().setStages(Array(lr))
  val model = pipeline.fit(dataset)

  def predict(n:Int) =println(s"Coefficients: ${model.stages} Intercept: ${model.params}")
  val predictions = model.transform(testdataset)

  val AUPR =new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderPR").evaluate(predictions)
  val AUC = new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderROC").evaluate(predictions)
  println(s"AUPR: $AUPR, AUC: $AUC")


/*  val evaluator =new BinaryClassificationEvaluator().setLabelCol("label").setRawPredictionCol("prediction").setMetricName("areaUnderROC")
  val AUC = evaluator.evaluate(predictions)*/
}
