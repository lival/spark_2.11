import java.io.File
import java.net.URL

/**
  * Created by lh on 2018/1/2.
  */
object testDDD {
  def main(args: Array[String]):Unit= {
    val persistCodePath=" at flatMapValues at Bagel.scala:235"
    val patternLineNum="""[0-9]+(?=[^0-9]*$)""".r //字符串最后面的数字
    var perLineNum= patternLineNum.findFirstIn(persistCodePath).mkString
  //  println(intLineNum) 235
    var perTransform= persistCodePath.substring(persistCodePath.indexOf("at")+3,persistCodePath.lastIndexOf("at"))
   // println(perTransform) flatMapValues
   var perSourcecode= persistCodePath.substring(persistCodePath.lastIndexOf("at")+3,persistCodePath.lastIndexOf(":"))
    println(perSourcecode) //Bagel.scala
    val directory = new File("..Bagel.scala")
    println("resource1 => " + directory.getAbsolutePath) //D:\liGitRes\SparkBranch\spark\Bagel.scala
    println("resource2 => " +directory.getParentFile) //directoy.
  }
}
