import java.io.LineNumberReader

import scala.io.Source
import scala.util.control.Breaks._
/**
  * Created by lh on 2017/12/27.
  */
object ReadLine{
  def main(args: Array[String]):Unit= {
   /* val file=Source.fromFile("D:/liGitRes/TestCase/Spark2661Test/Bagel.scala","utf-8")
    var intLineNum=235;*/
   val file=Source.fromFile("D:/spark/TestCase/Spark16697Test/LDAOptimizer.scala","utf-8")
    var intLineNum=466;
    val lineIterator=file.getLines.toList
    var fixCode=lineIterator(intLineNum-1)//index from 0,eg Index is 0 where Line is 1
    while(intLineNum>0){
          if(!fixCode.contains("=")){
            fixCode=lineIterator(intLineNum-1)
            }
      intLineNum=intLineNum-1
    }
    println(fixCode)
    //processed有可能赋值给别的RDD变量  lastRDD = processed
    var string2=fixCode.substring(0,fixCode.indexOf("=")-1)
    if(string2.contains(":")){
      string2=string2.substring(0,string2.indexOf(":"))
    }
    val index=string2.lastIndexOf(" ")
    val rddName=string2.substring(index+1,string2.length)
    println(rddName)
    var lineNum=lineIterator.size
    var fixCode2=""
    while(lineNum>0){
      if(lineIterator(lineNum-1).endsWith(rddName)){
        fixCode2=lineIterator(lineNum-1)
        val rddNameStr=fixCode2.substring(fixCode2.indexOf("="),fixCode2.length()).trim()
        if(rddNameStr==rddName){
          fixCode2=lineIterator(lineNum-1)
          println("lineNum:"+lineNum)
        }else{fixCode2=""}
      }
      lineNum=lineNum-1
    }
    fixCode=fixCode+","+fixCode2
    println(fixCode)
    file.close
  }
}
