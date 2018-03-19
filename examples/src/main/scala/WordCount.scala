import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lh on 2017/5/22.
  */
object WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(conf)
    val file = sc.textFile("D:/spark/README.md")
    val counts = file.flatMap(line => line.split(" "))
      .map(word => (word,1))
      .reduceByKey(_ + _).cache()
    counts.count()
   // counts.count()
    print("WordNumber:"+ counts.count())
  //sc.stop()
    System.in.read()
  }
}