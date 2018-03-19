import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lh on 2017/5/5.
  */

//scalastyle:off println
object SparkCountTest {
  def main(args: Array[String]):Unit={
    val conf = new SparkConf().setMaster("local").setAppName("SparkCountTest")
    val sc = new SparkContext(conf)
    val rdd1 = sc.makeRDD(1 to 10).filter(_ % 4 == 0).map(Math.sqrt(_)).map(e1 => (e1.toInt, e1)).cache()
    val rddPatterns="RDD"
    val rdd2 = rdd1.groupByKey
    val rdd3 = rdd1.coalesce(2)
    rdd2.count
    rdd3.count
    rdd1.unpersist()
  /*  sc.getPersistentRDDs.foreach( r => println( "After unPersistentRDDs:"+r._2.toString))
    rdd3.count()
    sc.getPersistentRDDs.foreach( r => println( "After countunPersistentRDDs:"+r._2.toString))*/
  }
}
// scalastyle:on println