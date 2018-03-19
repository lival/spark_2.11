import scala.collection.mutable.ArrayBuffer

/**
  * Created by lh on 2017/12/25.
  */
object caseTest {
  def main(args: Array[String]): Unit = {
    object PatternMatching extends App {
      var list = new ArrayBuffer[Int]()
      var x = 0
      for (i <- 1 to 100) {
        i match {
          //后面可以跟表达式
          case 10 => x = 10
          case 50 => println(50)
          case 80 => println(80)
          case _ if (i % 4 == 0) => list.append(i)
          case _ if (i % 3 == 0) => println(i + ":能被3整除")
          case _ =>
        }
      }
      println(x)
    }
  }
}
