import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by lh on 2017/5/22.
  * 每秒接收localhost地址，端口9999，接收到的字符串分隔
  */
object testDstream {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("testDstream")
    val sc = new SparkContext(conf)
    val rdd1 = sc.makeRDD(1 to 10).filter(_ % 4 == 0).map(Math.sqrt(_)).map(e1 => (e1.toInt, e1)).cache()
    val rdd2 = rdd1
    rdd2.collect
    System.in.read()
  }
}
