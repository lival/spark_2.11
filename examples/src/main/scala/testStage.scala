import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by lh on 2017/5/16.
  */
object testStage {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("testStage")
    val sc = new SparkContext(conf)
    val rdd1 = sc.makeRDD(Seq(("a",1), ("b",1)), 2)
    rdd1.map{e1 =>Thread.sleep(3000);e1}.count()
    rdd1.foreach(f=>println(f))
    //(b,1)
    //(a,1)
    val rdd2 = sc.makeRDD(Seq(("a",1), ("a",3), ("b",1), ("c",1), ("d",1), ("e",1)), 4)
    rdd2.map(e1=>(e1,1))
    rdd2.reduceByKey(_+_)
    rdd2.foreach(f=>println(f))
    //(a,3)
    //(b,1)
    //(d,1)
    //(e,1)
    //(a,1)
    //(c,1)
    val rdd3 = rdd1.join(rdd2,1)
    rdd3.foreach(f=>println(f))
    rdd3.collect()
    //(a,(1,1))
    //(a,(1,3))
    //(b,(1,1))

    System.in.read()
    //  Thread.sleep(30000000)
  }
}
