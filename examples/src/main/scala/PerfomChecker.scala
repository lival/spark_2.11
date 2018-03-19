import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lh on 2017/12/22.
  */
object PerfomChecker {
 // case class checkPersist()

  def main(args: Array[String]) {

    val z=myclass("cat",3) ;
    println("z:"+z)
  }

  case class myclass(name: String, id: Integer = 0) {
    def makemessage = "Hi, I'm a " + name + "with Id" + id
  }
}
