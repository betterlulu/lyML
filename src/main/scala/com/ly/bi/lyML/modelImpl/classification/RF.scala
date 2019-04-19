package com.ly.bi.lyML.modelImpl.classification

import com.ly.bi.lyML.model
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.sql.DataFrame

class RF(dataset: DataFrame,testdataset:DataFrame) extends model{

  val model = new RandomForestClassifier().setLabelCol("label").setFeaturesCol("features").fit(dataset)

  def predict(n:Int) =println(s"featureImportances: ${model.featureImportances}")
  val predictions = model.transform(testdataset: DataFrame)


}
