package com.ly.bi.lyML

import org.apache.spark.sql.SparkSession;

/**
  * Execute the lyML distributed Machine Learning Provided by Spark.
  * Requires an standardized structure file and output directory in hdfs or hive table
  */
object Main {
  def main(args: Array[String]): Unit = {
    // Parse Command line options
    val parser = new scopt.OptionParser[Config](this.getClass().toString()){
      opt[String]('i',"input") action {(x,c)=> c.copy(input=x)}  text("input file or path  Required.")
      opt[String]('o',"output") action {(x,c)=> c.copy(output=x)} text("output path Required")
      opt[String]('m',"master") action {(x,c)=> c.copy(master=x)} text("spark master, local[N] or spark://host:port default=local")
      opt[String]('h',"sparkhome") action {(x,c)=> c.copy(sparkHome=x)} text("SPARK_HOME Required to run on cluster")
      opt[String]('n',"jobname") action {(x,c)=> c.copy(appName=x)} text("job name")
      opt[Int]('p',"parallelism") action {(x,c)=> c.copy(parallelism=x)} text("sets spark.default.parallelism and minSplits on the edge file. default=based on input partitions")
      opt[Int]('x',"minprogress") action {(x,c)=> c.copy(minProgress=x)} text("Number of vertices that must change communites for the algorithm to consider progress. default=2000")
      opt[Int]('y',"progresscounter") action {(x,c)=> c.copy(progressCounter=x)} text("Number of times the algorithm can fail to make progress before exiting. default=1")
      opt[String]('d',"delimiter") action {(x,c)=> c.copy(delimiter=x)} text("specify input file edge delimiter. default=\",\"")
      opt[String]('j',"jars") action {(x,c)=> c.copy(jars=x)} text("comma seperated list of jars")
      opt[Boolean]('z',"ipaddress") action {(x,c)=> c.copy(ipaddress=x)} text("Set to true to convert ipaddresses to Long ids. Defaults to false")
      arg[(String,String)]("<property>=<value>....") unbounded() optional() action {case((k,v),c)=> c.copy(properties = c.properties :+ (k,v)) }
    }

    var sampleFile, outputdir,master,jobname,jars,sparkhome ,delimiter = ""
    var properties:Seq[(String,String)]= Seq.empty[(String,String)]
    var parallelism,minProgress,progressCounter = -1
    var ipaddress = false
    parser.parse(args,Config()) map {
      config =>
        sampleFile = config.input
        outputdir = config.output
        master = config.master
        jobname = config.appName
        jars = config.jars
        sparkhome = config.sparkHome
        properties = config.properties
        parallelism = config.parallelism
        delimiter = config.delimiter
        minProgress = config.minProgress
        progressCounter = config.progressCounter
        ipaddress = config.ipaddress
        if (sampleFile == "" || outputdir == "") {
          println(parser.usage)
          sys.exit(1)
        }
    } getOrElse{
      sys.exit(1)
    }

    // set system properties
    properties.foreach( {case (k,v)=>
      println(s"System.setProperty($k, $v)")
      System.setProperty(k, v)
    })

    // Create the spark sparksession
    println(s"sparkSession = new SparkContext($master,$jobname,$sparkhome,$jars)")
    val ss: SparkSession = SparkSession.builder().master(master).appName(jobname).enableHiveSupport().getOrCreate()  //.config("spark.default.parallelism", 500)
    ss.sparkContext.setLogLevel("ERROR")
    println("guhualu mark")

    val traindata = ss.sparkContext.textFile(sampleFile).map(_.split(delimiter))//.repartition(200)//.filter(x => x(1).toInt==6)  //.map(_.trim())
    val testdata = ss.sparkContext.textFile(sampleFile).map(_.split(delimiter))//.filter(x => x(1).toInt>6)
    println(sampleFile)

    val runner = new lyMLRunner(minProgress,progressCounter,outputdir)
    runner.run(ss, traindata,testdata)
  }
}




