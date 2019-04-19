package com.ly.bi.lyML

/**
  * Specify command line options and their defaults
  * @param input: 输入hive表路径，或hdfs数据文件
  * @param output: 输出hive表，或hdfs路径
  * @param master: master或local
  * @param appName: 设置spark 应用的名字
  * @param jars: 加载jar包
  * @param sparkHome: 设置默认spark环境
  * @param parallelism: 设置并行度
  * @param delimiter: 设置分隔符
  * @param minProgress: 最小进程数
  * @param progressCounter: 进程计数
  * @param ipaddress: ip地址
  * @param properties: 属性
  */
case class Config(
                   input:String = "/data/train/tmp_train/csgrabticketqueryinfo_timelabel/year=2019/month=01/day=13",
                   output: String = "tmp_train.gu_hhh99",
                   master:String = "yarn",
                   appName:String = "lyML",
                   jars:String = "",
                   sparkHome:String = "",
                   parallelism:Int = -1,
                   delimiter:String = "\001",
                   minProgress:Int = 2000,
                   progressCounter:Int = 1,
                   ipaddress: Boolean = false,
                   properties:Seq[(String,String)] = Seq.empty[(String,String)]
                 )
