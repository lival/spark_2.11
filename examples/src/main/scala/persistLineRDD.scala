import scala.io.Source
import scala.collection.mutable.Map

/**
  * Created by lh on 2018/1/2.
  */
object persistLineRDD {
  def main(args: Array[String]):Unit= {
    val file = Source.fromFile("D:/liGitRes/TestCase/Spark2661Test/Bagel.scala", "utf-8")
    var intLineNum = 235;
    val lineIterator = file.getLines.toList
    var s1 = new scala.collection.mutable.HashMap[String, String]
    var fixCode = lineIterator(intLineNum - 1) //index from 0,eg Index is 0 where Line is 1
    while (intLineNum > 0) {
      if (!fixCode.contains("=")) {
        fixCode = lineIterator(intLineNum - 1)
      }
      intLineNum = intLineNum - 1
    }
    //println(FetchRDDName(fixCode))
    var rddName=FetchRDDName(fixCode)
    var lineNum=lineIterator.size
    var fixCode2=""
    while(lineNum>0){
      if(lineIterator(lineNum-1).endsWith(rddName)){
        fixCode2=lineIterator(lineNum-1)
    //    println("lineNum:"+lineNum)
      }
      lineNum=lineNum-1
    }

    s1.+=((FetchRDDName(fixCode)->fixCode))
    s1.+=((FetchRDDName(fixCode2)->fixCode2))
    println(s1)
    s1.keys.foreach(println)
    var stringRDDName=s1.keySet.mkString(",")
    println("stringRDDName:"+stringRDDName)
  }

  def FetchRDDName(fixCode:String):String={
    var rddName=""
    var string1 =fixCode
    // println("FetchRDDName string1:"+string1)
    val string2=string1.substring(0,string1.indexOf("=")-1)
    val index=string2.lastIndexOf(" ")
    rddName=string2.substring(index+1,string2.length)
    //println(rddName)
    return rddName
  }
}
