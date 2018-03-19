import org.apache.spark.{SparkConf, SparkContext}
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

/**
  * Created by lh on 2017/7/7.
  */
object ExtractRDDIds {
  def main(args: Array[String]):Unit= {

   val conf = new SparkConf().setMaster("local").setAppName("ExtractRDDIds")
    val sc = new SparkContext(conf)
    val rddDepRDD= ArrayBuffer[Int](); //声明一个变量数组，存取IDs

/*   var  stringDeps="MapPartitionsRDD[3] at map at testDep.scala:16 [Memory Deserialized 1x Replicated] " +
     "|  MapPartitionsRDD[2] at map at testDep.scala:16 [Memory Deserialized 1x Replicated] " +
     " |  MapPartitionsRDD[1] at filter at testDep.scala:16 [Memory Deserialized 1x Replicated] " +
     "|  ParallelCollectionRDD[0] at makeRDD at testDep.scala:15 [Memory Deserialized 1x Replicated]";*/
      //val file=Source.fromFile("E:\\test.txt")
      val file=Source.fromFile("G:\\test.txt")
      //  file.foreach(print)
       val stringDeps=file.mkString; //把整个文件读取成一个字符串
        // println("stringDeps:"+stringDeps)
           val pattern1="""(RDD\[\d+\])""".r
           val m= pattern1.findAllIn(stringDeps).toArray
             // println(m.mkString(",")) //RDD[3],RDD[2],RDD[1],RDD[0]
          val rddDeps=m.mkString(",")
          val pattern2="""\d+""".r   //3,2,1,0
          val n= pattern2.findAllIn(rddDeps).toArray.mkString(",")
             println("RddDeps:"+n)
        }
        }
