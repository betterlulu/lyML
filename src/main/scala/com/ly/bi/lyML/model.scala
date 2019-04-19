package com.ly.bi.lyML
import org.apache.spark.sql.DataFrame

trait model {

  val predictions:DataFrame
  def predict(n:Int)

}
