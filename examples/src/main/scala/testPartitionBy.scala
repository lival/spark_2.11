import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
  * Created by lh on 2017/7/13.
  */
object testPartitionBy {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("testPartitionBy")
    val sc = new SparkContext(conf)
    //val logFileA = sc.textFile("C:/Users/lh/Desktop/10G/tpch.log").map((_,1))
    val logFileA = sc.textFile("C:/Users/lh/Desktop/10G/tpch.log").map((_,1))
    println( "Before new partition:"+logFileA.partitions.size )
    val logDataB=logFileA.partitionBy(new HashPartitioner(2))
    println( "After new partition:"+logDataB.partitions.size )
    logDataB.partitions.foreach { partition =>
         println("index:" + partition.index + "  hasCode:" + partition.hashCode())
       }
     val logFileC=logDataB.filter(_._1.startsWith("ERROR"))
     val logFileD=logFileC.filter(_._1.length>30)
    logFileC.cache().count();
    logFileD.count();
    val logFileD1=logFileC.unpersist();
    logFileD1.count()
  }

}
