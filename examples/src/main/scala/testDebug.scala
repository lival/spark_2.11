import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lh on 2017/12/26.
  */
object testDebug {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("testDebug")
    val sc = new SparkContext(conf)
    val input = sc.parallelize(1 to 10)
    val repartitioned = input.repartition(2)
    val sum = repartitioned.sum
    System.in.read()
  }
}
