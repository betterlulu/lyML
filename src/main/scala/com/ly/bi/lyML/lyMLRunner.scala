package com.ly.bi.lyML

import org.apache.spark.SparkContext

/**
  * Execute the lyML and save the output of preditions in hdfs at each level.
  * Can also save locally if in local mode.
  *
  * See lyMLHarness for algorithm details
  */
class lyMLRunner(minProgress:Int, progressCounter:Int, outputdir:String) extends lyMLHarness(minProgress:Int,progressCounter:Int){

  var qValues = Array[(Int,Double)]()

  def saveLevel(sc:SparkContext,level:Int,q:Double) = {


    //graph.vertices.map( {case (id,v) => ""+id+","+v.internalWeight+","+v.community }).saveAsTextFile(outputdir+"/level_"+level+"_vertices")
    //graph.edges.mapValues({case e=>""+e.srcId+","+e.dstId+","+e.attr}).saveAsTextFile(outputdir+"/level_"+level+"_edges")

    // overwrite the q values at each level
    //sc.parallelize(qValues, 1).saveAsTextFile(outputdir+"/qvalues"+level)
  }

}
