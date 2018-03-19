import scala.io.Source

/**
  * Created by lh on 2017/12/28.
  */
object cacheUnpersist {
  def main(args: Array[String]):Unit= {
    val logPath="G:/logInfo1.txt"
    val file=Source.fromFile(logPath, "utf-8")
    val logCode=file.mkString;
      println(logCode) //print logInfo
 /*   val lineIterator=file.getLines.toList
    for(line<-lineIterator){
      if(line.contains("cache()")||line.contains("persist()")){
        var stringLine=line
        // println("Locate the Line:"+stringLine)
        var pattern1="""[0-9]+(?=[^0-9]*$)""".r //字符串最后面的数字
        intLineNum= Integer.parseInt(pattern1.findAllIn(stringLine).mkString)
      }
    }*/
  }
}
