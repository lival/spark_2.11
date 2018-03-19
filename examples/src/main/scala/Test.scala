import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lh on 2018/1/25.
  */
object Test extends App{
 val conf=new SparkConf().setAppName("Test")
  val sc=new SparkContext(conf)
 val count= sc.parallelize(1 to 10).map(l=>l.toString)
  funcs.f2(count)
  funcs.f3(count)
  println("hehe:"+  funcs.f2(count))
  object funcs{
    def f2(rdd:RDD[String]): Unit ={
          rdd.map(l=>l)
    }
    def f3(rdd:RDD[String]): Unit ={
      f4(rdd)
    }
    def f4(rdd:RDD[String]): Unit ={
      rdd.map(l=>l)
    }
  }

}
