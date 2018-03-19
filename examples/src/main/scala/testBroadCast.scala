import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.collection.mutable.ListBuffer


/**
  * Created by lh on 2017/12/11.
  */
object testBroadCast {
  def main(args: Array[String]): Unit = {
      val conf=new SparkConf().setMaster("local").setAppName("testBroadCast")
    val sc=new SparkContext(conf)
//首先生成了一个集合变量，把这个变量通过sparkContext的broadcast函数进行广播，最后在rdd的每一个partition迭代时，使用这个广播变量。
    val factor = List[Int](1,2,3);
    val factorBroadcast = sc.broadcast(factor)

    val nums = Array(4,5,6,7,8,9)
    val numsRdd = sc.parallelize(nums,3)

    val list = new ListBuffer[List[Int]]()
    val resRdd = numsRdd.mapPartitions(ite =>{
      while (ite.hasNext){
        list+=ite.next()::(factorBroadcast.value)
      }
      list.iterator
    })
    resRdd.foreach(res => println(res))

    /**结果：
    List(4, 1, 2, 3)
    List(5, 1, 2, 3)
    List(6, 1, 2, 3)
    List(7, 1, 2, 3)
    List(8, 1, 2, 3)
    List(9, 1, 2, 3)
      */
  //  factorBroadcast.destroy()  出现Error
    factorBroadcast.unpersist()
    resRdd.foreach(res => println(res))
  }
}
