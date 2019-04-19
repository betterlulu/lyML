package com.ly.bi.lyML

import com.ly.bi.lyML.modelImpl.classification._
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag
import org.apache.spark.ml.feature.{LabeledPoint, MinMaxScaler}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
 * Coordinates execution of the lyML distributed Machine Learning on Spark.
 */
class lyMLHarness(minProgress:Int, progressCounter:Int)extends Serializable {
  
  def run[VD: ClassTag](spark:SparkSession,traindata:RDD[Array[String]], testdata:RDD[Array[String]]) = {

		def xx(x: Array[String]): Array[Double] = {
			val k = new Array[Double](x.length - 1)
			for(i <- (1 to x.length - 1)){
				k(i - 1) = x(i).toDouble
			}
			return k
		}

		val scaler = new MinMaxScaler()
			.setInputCol("features")
			.setOutputCol("featuress")
		val LabeledPointRdd = traindata.map(x => LabeledPoint(x(0).toInt, Vectors.dense(xx(x))))

		val dataset = spark.createDataFrame(LabeledPointRdd)
		val scalerModel = scaler.fit(dataset)
		val Data1 =scalerModel.transform(dataset)

		val LabeledPointRdd2 = testdata.map(x => LabeledPoint(x(0).toInt, Vectors.dense(xx(x))))
		val dataset2 = spark.createDataFrame(LabeledPointRdd2)
		val Data2 =scalerModel.transform(dataset2)

		var Model:model = null
		if(minProgress==0){
			Model=new LR(Data1,Data2)
		}else if(minProgress==1){
			Model=new GBT(Data1,Data2)
		}else{
			Model=new XGB(Data1,Data2)
		}

		val predictions=Model.predictions
		Model.predict(1)

		predictions.groupBy("prediction").count().show()
		predictions.groupBy("prediction").sum("label").show()

	}
}