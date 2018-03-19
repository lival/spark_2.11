
import scala.io.Source

/**
  * Created by lh on 2017/12/28.
  */
object DetectedRDD {
  def main(args: Array[String]): Unit = {

    println("=========================================================================")

    val projectPath="D:/spark/TestCase/"
  /*  val sourceCodePath=projectPath+"SparkCountTest/SparkCountTest.scala"
   val logPath =projectPath+"SparkCountTest/logInfo.txt"*/
    /*val sourceCodePath=projectPath+"Spark2661Test/BagelSuite.scala"
    val logPath = projectPath+"Spark2661Test/logInfo.txt"*/
    /*val sourceCodePath=projectPath+"Spark16696Test/BisectingKMeansExample.scala"
    val logPath =projectPath+"Spark16696Test/logInfo.txt"*/
   /* val sourceCodePath=projectPath+"Spark10182Test/SVMSuite.scala"
    val logPath =projectPath+"Spark10182Test/logInfo.txt"*/
   /* val sourceCodePath=projectPath+"Spark7100Test/GradientBoostedTreesSuite.scala"
    val logPath =projectPath+"Spark7100Test/logInfo.txt"*/
    /*val sourceCodePath=projectPath+"Spark3918Test/RandomForestSuite.scala"
    val logPath =projectPath+"Spark3918Test/logInfo.txt"*/
   /* val sourceCodePath=projectPath+"Spark2025Test/PregelSuite.scala"
    val logPath =projectPath+"Spark2025Test/logInfo.txt"*/
    val sourceCodePath=projectPath+"Spark3290Test/SVDPlusPlusSuite.scala"
    val logPath =projectPath+"Spark3290Test/logInfo.txt"
   /* val sourceCodePath=projectPath+"Spark12655Test/ConnectedComponentsSuite.scala"
    val logPath =projectPath+"Spark12655Test/logInfo.txt"*/
   /* val sourceCodePath=projectPath+"Spark16697Test/LDASuite.scala"
    val logPath =projectPath+"Spark16697Test/logInfo.txt"*/
    /*val sourceCodePath=projectPath+"Spark16880Test/Layer.scala"
     val logPath =projectPath+"Spark16880Test/logInfo.txt"*/
    /*  val sourceCodePath=projectPath+"Spark17559Test/PeriodicGraphCheckpointer.scala"
       val logPath =projectPath+"Spark17559Test/logInfo.txt"*/
    var rddDependency = FetchDAG(logPath)
   //var rddDependency="5,4,3,2,1,0,6,4,3,2,1,0,7,4,3,2,1,0,9,8,1,0,10,8,1,0"
    println(rddDependency)

    /** Detect valueable RDD using algorithm */
    val findDupSeqs=new FindDupSeqs()
    var ResultList=findDupSeqs.findLongestDupString(rddDependency)
   //println(ResultList)
    var logseqResult=ResultList.get("maxDupStr").toString
    println("logseqResult: "+logseqResult);
    println("The number of each RDD:"+countNum(rddDependency))
   if(logseqResult.length!=0){
     var logsResult=logseqResult.split(",").map(a=>a.toInt) //change string into int ，then get max
     var perRddMax=logsResult.max
     var perRddFirst=logsResult.headOption.get //fetch the first element
     println("perRddMax:"+perRddMax+";  perRddFirst:"+perRddFirst)
     var perRdd=perRddFirst
     if(perRddMax!=perRddFirst){
       perRdd=lastPerRdd(rddDependency,perRddMax,perRddFirst) //match longest string and countNum
     }

     var persistRdd="RDD["+ perRdd+"" +"]"
     println("=========================================================================")
     /**Detect persist RDD*/
     val persistCodePath=LocFromRDDId(logPath,persistRdd)
     println("The best location for using cache strategy is "+persistCodePath)// at flatMapValues at Bagel.scala:235
     val patternLineNum="""[0-9]+(?=[^0-9]*$)""".r //字符串最后面的数字
     var perLineNum=Integer.parseInt( patternLineNum.findFirstIn(persistCodePath).mkString) //235
     //    println("perLineNum:"+perLineNum)
     var perTransform= persistCodePath.substring(persistCodePath.indexOf("at")+3,persistCodePath.lastIndexOf("at")) //mapValues
     var perSourcecode= persistCodePath.substring(persistCodePath.lastIndexOf("at")+3,persistCodePath.lastIndexOf(":")) //Bagel.scala
     println("You can change code at Line:"+perLineNum+",at "+perSourcecode+" at tranform  "+perTransform+" like this: "+perTransform+".persist()")


     /**Detect persist and unpersist in pair  * */
     println("=========================================================================")
     //  var fixpersistCode=ReadLine(persourceCodePath,intLineNum)
     // println("You can change code at Line:"+intLineNum+",like this:"+fixpersistCode+".cache()")
  /*   var persourceCodePath=projectPath+"SparkCountTest/"+perSourcecode*/
   /*   var persourceCodePath=projectPath+"Spark2661Test/"+perSourcecode*/
      /*var persourceCodePath=projectPath+"Spark16696Test/"+perSourcecode*/
    /* var persourceCodePath=projectPath+"Spark10182Test/"+perSourcecode*/
     /*var persourceCodePath=projectPath+"Spark7100Test/"+perSourcecode*/
     /*var persourceCodePath=projectPath+"Spark3918Test/"+perSourcecode*/
/*     var persourceCodePath=projectPath+"Spark2025Test/"+perSourcecode*/
     var persourceCodePath=projectPath+"Spark3290Test/"+perSourcecode
     /*var persourceCodePath=projectPath+"Spark12655Test/"+perSourcecode*/
  /*   var persourceCodePath=projectPath+"Spark16697Test/"+perSourcecode*/
     /* var persourceCodePath=projectPath+"Spark16880Test/"+perSourcecode*/
     /* var persourceCodePath=projectPath+"Spark17559Test/"+perSourcecode*/
     //println("persourceCodePath:"+persourceCodePath)
     //println("perLineNum:"+perLineNum)
     var stringRDDName=ReadLine(persourceCodePath,Integer.parseInt(perLineNum.toString))
     //  var rddName=fixCodeName.substring(fixCodeName.lastIndexOf(","),fixCodeName.length)
     println("perisitrddName:"+stringRDDName)
     var unperExist=DetectUnpersist(persourceCodePath,stringRDDName)
     // println("unperExist:"+unperExist)
     if(!unperExist){
       println("ERROR! : "+stringRDDName+" should be unpersist! Please check.")
     }

     /**Detect the best location of using unpersist
       * */
    /* println("=========================================================================")
     val unpersistRdd=unpersistRddId(rddDependency,logseqResult)
     println("unpersistRdd:"+unpersistRdd)
     var unpRdd=unpersistRdd+""
     var unpRddId="RDD["+ unpRdd +"]"
     val unpersistCodePath=LocFromRDDId(logPath,unpRddId)
     println("The best location for using unpersist is after  "+unpersistCodePath)
     println("=========================================================================")*/
   }
    else{
     println("No RDD is necessary to persist!")
     println("=========================================================================")
   }
  }

/**Obtain lineage graph form the log after the first running.
  * */
  def FetchDAG( logPath:String) : String = {
    var rddDependency=""
    val file=Source.fromFile(logPath)
    val stringDeps=file.mkString;
    val pattern0="""((\+\-\(2+\))|(\+\-\(1+\))|(\+\-\(3+\)))""".r
    val n= pattern0.findAllIn(stringDeps).toArray.mkString//+-(2)  +-(1)
    val unStage0=pattern0.replaceAllIn(stringDeps, ",").mkString
    val pattern1="""(\(1\)|\(2\)|\(3\))""".r
    val m= pattern1.findAllIn(unStage0).toArray.mkString//(1)(2)
    val unStage1=pattern1.replaceAllIn(unStage0, ";").mkString
    val pattern2="""\|""".r
    val q= pattern2.findAllIn(unStage1).toArray.mkString//|
    val unStage2=pattern2.replaceAllIn(unStage1, ",").mkString
    val pattern3=""";?.*RDD[1-9]?\[\d*\]""".r
    val w= pattern3.findAllIn(unStage2).toArray.mkString
    val pattern4 = """([a-z]+)|([A-Z]+)|\[]|\|""".r
    val a= pattern4.replaceAllIn(w,"").mkString
    val pattern5 = """\[\d*?\]|;|,""".r
    val s= pattern5.findAllIn(a).toArray.mkString

    val pattern6 = """\,+""".r
    val z= pattern6.replaceAllIn(s,",").mkString

    val pattern7 = """\];,""".r
    val x= pattern7.replaceAllIn(z,";").mkString

    val pattern8 = """(\[|\])""".r
    val r= pattern8.replaceAllIn(x,"").mkString

    val pattern9 ="""\;""".r
    rddDependency = pattern9.replaceAllIn(r, ",").mkString
    rddDependency=rddDependency.substring(1,rddDependency.length)
    //println(rddDependency)
    return rddDependency
  //  println(stringDeps) //print logInfo
  }
  /**Count number of each RDD
    * */
  def countNum(str: String) = {
    var map = Map.empty[String, Int]
    for(word <- str.split("[,:!]|;")){
      if(map.contains(word))
        map += (word -> (map(word)+1))
      else
        map += (word -> 1)
    }
    // map.toSeq.sortBy(_._2) //升序排列value
    //map.toSeq.sortBy(_._1)// 升序排序 key
    map.toSeq.sortWith(_._2>_._2) //降序排序 value  ((1,2), (0,2), (2,2), (3,2), (4,1), (5,1))

  }
  def lastPerRdd(str: String,maxStr:Integer,fisrtStr:Integer):Integer = {
    var map = Map.empty[String, Int]
    for(word <- str.split("[,:!]|;")){
      if(map.contains(word))
        map += (word -> (map(word)+1))
      else
        map += (word -> 1)
    }
   //println("maxStr:"+maxStr+";fisrtStr:"+fisrtStr)
    var maxRdd=map(maxStr.toString)
    var firstRdd=map(fisrtStr.toString)
    var perRdd=fisrtStr
    if(maxRdd>firstRdd){
      perRdd=maxRdd
    }
 //   println("rdds:"+perRdd)
   return perRdd
  }
  /**Locate the line number from the log file according to RddId.
    * */
  def LocFromRDDId(logPath:String,RddId:String):String = {
val file = Source.fromFile(logPath, "utf-8")
    var intLineNum=9999999;
    var persistCodePath="";
   //println("RDDId:"+RddId) //RDD[175] or RDD2[175]
   var RDD2Id=RddId.substring(4,RddId.length-1)
    RDD2Id="RDD2["+RDD2Id+"]"
    val lineIterator=file.getLines.toList
    for(line<-lineIterator){
      if(line.contains(RddId)||line.contains((RDD2Id))){
        var stringLine=line
        /**Locate the Line:(1) MapPartitionsRDD[6] at flatMapValues at Bagel.scala:235 [Disk Memory Deserialized 1x Replicated]
-1
Locate the Line: |  MapPartitionsRDD[6] at flatMapValues at Bagel.scala:235 []
60
          * */
        if(stringLine.lastIndexOf("[]")>0){ //找到Locate the Line: |  MapPartitionsRDD[6] at flatMapValues at Bagel.scala:235 []
          var pattern1="""[0-9]+(?=[^0-9]*$)""".r //字符串最后面的数字
          intLineNum= Integer.parseInt(pattern1.findFirstIn(stringLine).mkString)
         // println("intLineNum:"+intLineNum)
          persistCodePath=stringLine.substring(stringLine.indexOf("at"),stringLine.lastIndexOf("[]"))
        //  println(persistCodePath)
        }
      }
    }
    return persistCodePath
  }

/**Read code which is need to be modified according to the line number.
  * */
  def ReadLine(sourceCodePath:String,lineNumber:Integer):String = {
    val file=Source.fromFile(sourceCodePath,"utf-8")
    var intLineNumRead=lineNumber;
    val lineIterator = file.getLines.toList
    var s1 = new scala.collection.mutable.HashMap[String, String]
    var fixCode = lineIterator(intLineNumRead - 1) //index from 0,eg Index is 0 where Line is 1
    //println("fixCode:"+fixCode)
    while (intLineNumRead > 0) {
      if (!fixCode.contains("=")) {
        fixCode = lineIterator(intLineNumRead - 1)
      }
      intLineNumRead = intLineNumRead - 1
    }
    //println(FetchRDDName(fixCode))
   // println("fixCdoe:"+fixCode)
    var rddName=FetchRDDName(fixCode)
    var lineNum=lineIterator.size
    var fixCode2=""
    while(lineNum>0){
  /*    if(lineIterator(lineNum-1).endsWith(rddName)){
        fixCode2=lineIterator(lineNum-1)
            println("lineNum:"+lineNum)
      }*/

      if(lineIterator(lineNum-1).endsWith(rddName)&&lineIterator(lineNum-1).contains("=")){
        fixCode2=lineIterator(lineNum-1)
        println("fixCode2:"+fixCode2)
        val rddNameStr=fixCode2.substring(fixCode2.indexOf("="),fixCode2.length()).trim()
        if(rddNameStr==rddName){
          fixCode2=lineIterator(lineNum-1)
          println("lineNum:"+lineNum)
        }else{fixCode2=""}
      }
      lineNum=lineNum-1
    }

    s1.+=((FetchRDDName(fixCode)->fixCode))
    if(fixCode2.length!=0){
      s1.+=((FetchRDDName(fixCode2)->fixCode2))
    }
   // println(s1)
    //s1.keys.foreach(println)
    var stringRDDName=s1.keySet.mkString(",")
    //println("stringRDDName:"+stringRDDName)
    return stringRDDName
  }
  /**Detect the best location of using unpersist
    * */
  def unpersistRddId(rddDependency:String,logseqResult:String): Integer ={
    //val string1="4,3,2,1,0,5,3,2,1,0"
    //val string2="3,2,1,0"
    val index=rddDependency.lastIndexOf(logseqResult)-1 //12
    val string3=rddDependency.substring(0,index) //4,3,2,1,0,5
   // println(string3)
    var unperRddId=string3.substring(string3.lastIndexOf(",")+1,string3.length)
    var unpersitRddId=Integer.parseInt(unperRddId)
    return unpersitRddId
  }

  /**Obtain RDDName is the source code form the fixCode
    * */
  def FetchRDDName(fixCode:String):String={
    var rddName=""
    var string1 =fixCode
    // println("FetchRDDName string1:"+string1)
    var string2=string1.substring(0,string1.indexOf("=")-1)
    if(string2.contains(":")){
      string2=string2.substring(0,string2.indexOf(":"))
    }else if(string2.contains(".")){
      if(string2.contains(".")){
        string2=string2.substring(0,string2.indexOf("."))
      }
    }
    val index=string2.lastIndexOf(" ")
    rddName=string2.substring(index+1,string2.length)
    //println(rddName)
    return rddName
  }

  def DetectUnpersist(sourceCodePath:String,rddName:String):Boolean = {
    val file = Source.fromFile(sourceCodePath, "utf-8")
    var s1 = new scala.collection.mutable.HashMap[String, String]
    val stringDeps=file.mkString;
    var unperExist = false
   // val rddName="processed,lastRDD"
    var rddName1 = rddName.split(",")
    for(c<-rddName.split(",")) {
      if (stringDeps.contains(c+".unpersist")) {
        unperExist = true
        //  println("This variable has been used unpersist:"+c+".unpersist()") //lastRDD实现了unpersist方法
      }
    }
   // println(unperExist)
    file.close
    return unperExist
  }


}




