  import org.apache.spark.{SparkConf, SparkContext}

  /**
    * Created by lh on 2017/6/1.
    *
    */
  //foreach and map difference is :foreach return void,map return collection.
  object foreachDiffMap {
    def main(args: Array[String]): Unit = {
      val conf = new SparkConf().setMaster("local").setAppName("test1")
      val sc = new SparkContext(conf)
      val someNumbers=List(-11,-3,5,9)
      val increase=(x:Int)=>x+2
      val b=someNumbers.foreach(increase)
      println(b.getClass())
      val c=someNumbers.map(increase)
      println(c.getClass())
      c.foreach((x:Int)=>print(x+" "))
      println()
      c.map((x:Int)=>print(x+" "))
    }
  }