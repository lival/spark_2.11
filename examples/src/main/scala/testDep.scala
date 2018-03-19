import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.util._
import org.apache.spark.rdd.RDD
import scala.collection.mutable.{ArrayBuffer, HashSet}
import java.util.LinkedList
import scala.collection.mutable
import scala.collection.mutable.HashMap
/**
  * Created by lh on 2017/5/8.
  */
object testDep {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("testDep")
    val sc = new SparkContext(conf)
    val rdd1 = sc.makeRDD(1 to 10)
      .filter(_ % 4 == 0).map(Math.sqrt(_)).map(e1 => (e1.toInt, e1)).cache()
    var rddDep1=new mutable.HashMap[Int,String]
    var rddDep2=new mutable.HashMap[Int, String]
    var rdd2 = rdd1.groupByKey
    // Add partitionsRdd into list
    val pattern="""(\S+RDD\[\d+\])""".r
    def extractKey(line:String,map:mutable.HashMap[Int,String]):(String,Int) = {
      pattern.findFirstIn(line) match {
        case Some(pattern(partitionsRDD)) =>{
          //  println("match:==="+partitionsRDD)
          val numberPattern="""\d+""".r
          val number= numberPattern.findFirstIn(partitionsRDD).get
          // println("======="+number)
          //(partitionsRDD)
          map(number.toInt) = partitionsRDD
          // list.add(number,partitionsRDD)
          (partitionsRDD,number.toInt)
        }
        case _ => {
          (null)
        }
      }
    }
    def extractPrint(strLink:Seq[Int]):(String) = {
      val msg = "%s{%s}"
      val sort=strLink.sorted.reverse
      msg.format(sort.head,sort.tail.mkString(","))
    }

    def extractRDD(rdd:RDD[_],rddDep:HashMap[Int,String]): Unit ={
      var rddNew=rdd.toDebugString
      var rddNewp:String=null
      if(rddNew.contains("ShuffledRDD")){
        rddNewp=rddNew.replaceAll("\\+-","\\|")
      }
      if(rddNewp==null)
        rddNew.split("\\|").map(line => {
          extractKey(line,rddDep)
        })else{
        rddNewp.split("\\|").map(line => {
          extractKey(line,rddDep)
        })
      }
      }

    extractRDD(rdd1,rddDep1)
    extractRDD(rdd2,rddDep2)
   /* println("RDD1-Dependencies:" + rdd1.toDebugString)
    println("RDD1-Lineage:"+extractPrint(rddDep1.keySet.toSeq))
    println("RDD2-Dependencies:" + rdd2.toDebugString)
    println("RDD2-Lineage:"+extractPrint(rddDep2.keySet.toSeq))*/
    var rddp1=sc.makeRDD(rddDep1.keySet.toSeq)
    var rddp2=sc.makeRDD(rddDep2.keySet.toSeq)
    var collect=rddp2.intersection(rddp1).collect()

    println("Common-RDDId:%d".format(rddp2.intersection(rddp1).collect().max))
   // val rddp1=sc.makeRDD(rddDep1.toArray())  // rddDep1.toArray()是什么？
    //println("rddp1:"+rddDep1.toArray())//rddp1:[Ljava.lang.Object;@4816c290
    //val rddp2=sc.makeRDD(rddDep2.toArray())
   //  rddp1.foreach(f=>println(f)) //打印rddp1里面的每个元素
  //    rddp2.foreach(f=>println(f))
    //println("共用的RDD:"+rddp2.intersection(rddp1))//共用的RDD:MapPartitionsRDD[12] at intersection at testDep.scala:54
  }
}

