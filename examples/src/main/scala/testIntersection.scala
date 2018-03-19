import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lh on 2017/5/8.
  */
object testIntersection {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setAppName("testIntersection")
    val sc = new SparkContext(conf)
    val all = sc.parallelize(1 to 10)
    all.collect()
    println("all:"+all.collect())
    val evens = sc.parallelize(2 to 8)
    evens.collect()
    all.intersection(evens).collect().sorted  //交集Array[Int] =Array[2,3,4,5,6,7,8]
     all.subtract(evens).collect().sorted  //差集Array[Int] =Array[1,9,10]
    all.union(evens).collect().sorted    //并集
    all.union(evens).distinct().collect().sorted //去重Array[Int] =Array[1，2,3,4,5,6,7,8，9,10]
  }
}
