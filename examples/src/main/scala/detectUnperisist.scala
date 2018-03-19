import scala.io.Source

/**
  * Created by lh on 2017/12/29.
  */
object detectUnperisist {
  def main(args: Array[String]):Unit= {
    val sourceCodePath="D:/spark/TestCase/Spark2661Test/Bagel.scala"
    var s1 = new scala.collection.mutable.HashMap[String, String]
    val file = Source.fromFile(sourceCodePath, "utf-8")
    val stringDeps=file.mkString;
    var unperExist = false
    val rddName="processed,lastRDD"
    var rddName1 = rddName.split(",")
    for(c<-rddName.split(",")) {
      if (stringDeps.contains(c+".unpersist")) {
        unperExist = true
      //  println(c+".unpersist()") lastRDD实现了unpersist方法
      }
    }
    println(unperExist)
    file.close
    return unperExist
  }
}
