import scala.io.Source

/**
  * Created by lh on 2017/12/27.
  */
object LocFromRDDId {
  def main(args: Array[String]):Unit= {
  //  val file = Source.fromFile("G:/test.txt", "utf-8")
  val file = Source.fromFile("D:/spark/TestCase/Spark2661Test/logInfo.txt", "utf-8")
    var intLineNum=9999999;
    var persistCodePath="";
    val RddId="RDD[6]"
    val lineIterator=file.getLines.toList
    for(line<-lineIterator){
      if(line.contains(RddId)){
        var stringLine=line
/**Locate the Line:(1) MapPartitionsRDD[6] at flatMapValues at Bagel.scala:235 [Disk Memory Deserialized 1x Replicated]
-1
Locate the Line: |  MapPartitionsRDD[6] at flatMapValues at Bagel.scala:235 []
60
  * */
        if(stringLine.lastIndexOf("[]")>0){ //找到Locate the Line: |  MapPartitionsRDD[6] at flatMapValues at Bagel.scala:235 []
          var pattern1="""[0-9]+(?=[^0-9]*$)""".r //字符串最后面的数字
          intLineNum= Integer.parseInt(pattern1.findFirstIn(stringLine).mkString)
          persistCodePath=stringLine.substring(stringLine.indexOf("at"),stringLine.lastIndexOf("[]"))
          println(persistCodePath)
        }
      }
    }
   return persistCodePath
}
}
