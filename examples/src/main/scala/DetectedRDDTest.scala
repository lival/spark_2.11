
import scala.io.Source

/**
  * Created by lh on 2017/12/28.
  */
object DetectedRDDTest  {
  def main(args: Array[String]): Unit = {

    val sourceCodePath="D:/spark/TestCase/Spark2661Test/BagelSuite.scala"
    val logPath = "D:/spark/TestCase/Spark2661Test/logInfo.txt"
    runThis(sourceCodePath,logPath)
  }
  def runThis(sourceCodePath:String,logPath:String) : Unit = {
    println("=========================================================================")

    var rddDependency = FetchDAG(logPath)
    println(rddDependency)

    /** Detect valueable RDD using algorithm */
    val findDupSeqs=new FindDupSeqs()
    var ResultList=findDupSeqs.findLongestDupString(rddDependency)
    println(ResultList)
    var logseqResult=ResultList.get("maxDupStr").toString
    println(logseqResult);
    println("The number of each RDD:"+countNum(rddDependency))
    var perRdd=logseqResult.max
    var persistRdd="RDD["+ perRdd+"" +"]"
    println("=========================================================================")
    /**Detect persist RDD*/
    val persistCodePath=LocFromRDDId(logPath,persistRdd)
    println("The best location for using cache strategy is "+persistCodePath)// at flatMapValues at Bagel.scala:235
    val patternLineNum="""[0-9]+(?=[^0-9]*$)""".r //字符串最后面的数字
    var perLineNum=Integer.parseInt( patternLineNum.findFirstIn(persistCodePath).mkString) //235
    var perTransform= persistCodePath.substring(persistCodePath.indexOf("at")+3,persistCodePath.lastIndexOf("at")) //mapValues
    var perSourcecode= persistCodePath.substring(persistCodePath.lastIndexOf("at")+3,persistCodePath.lastIndexOf(":")) //Bagel.scala
    println("You can change code at Line:"+perLineNum+",at "+perSourcecode+" at tranform  "+perTransform+" like this: "+perTransform+".cache()")


    /**Detect persist and unpersist in pair  * */
    println("=========================================================================")
    //  var fixpersistCode=ReadLine(persourceCodePath,intLineNum)
    // println("You can change code at Line:"+intLineNum+",like this:"+fixpersistCode+".cache()")
    var persourceCodePath="D:/spark/TestCase/Spark2661Test/"+perSourcecode //待输入。。。
    println("persourceCodePath:"+persourceCodePath)
    var stringRDDName=ReadLine(persourceCodePath,perLineNum)
    //  var rddName=fixCodeName.substring(fixCodeName.lastIndexOf(","),fixCodeName.length)
    println("perisitrddName:"+stringRDDName)
    var unperExist=DetectUnpersist(persourceCodePath,stringRDDName)
    // println("unperExist:"+unperExist)
    if(!unperExist){
      println("Level type:Warning! Category type: ErrorProne; "+stringRDDName+" should be unpersist! Please check.")
    }

    /**Detect the best location of using unpersist
      * */
    println("=========================================================================")
    val unpersistRdd=unpersistRddId(rddDependency,logseqResult)
    //println("unpersistRdd:"+unpersistRdd)
    var unpRdd=unpersistRdd+""
    var unpRddId="RDD["+ unpRdd +"]"
    val unpersistCodePath=LocFromRDDId(logPath,unpRddId)
    println("The best location for using unpersist is "+unpersistCodePath)
    println("=========================================================================")
  }
/**Obtain lineage graph form the log after the first running.
  * */
  def FetchDAG( logPath:String) : String = {
    var rddDependency=""
    val file=Source.fromFile(logPath)
    val stringDeps=file.mkString;
  //  println(stringDeps) //print logInfo
    val pattern1="""(\+\-\(1+\))""".r
    // val m= pattern1.findAllIn(stringDeps).toArray.mkString(",")//+-(1)
    val unStage=pattern1.replaceAllIn(stringDeps, "|").mkString //Do not distinguish stages
    val pattern2="""\(1+\)""".r
    val job=pattern2.replaceAllIn(unStage, ";").mkString
    // println("job:"+job)
    val pattern3 = """([a-z]+)|([A-Z]+)|\[]|\|""".r
    val m= pattern3.replaceAllIn(job,"").mkString
    val pattern4=""";?.*\[\d*\]""".r
    val n= pattern4.findAllIn(m).mkString
    val pattern5="""\[|\s""".r
    val q= pattern5.replaceAllIn(n,"")
    val pattern6="""\]""".r
    val t= pattern6.replaceAllIn(q,",")
    val pattern7=""",;""".r
    rddDependency= pattern7.replaceAllIn(t,";").substring(1)
    println(rddDependency) //4,3,2,1,0;5,3,2,1,0,
    val pattern0 ="""\;""".r
    rddDependency = pattern0.replaceAllIn(rddDependency, ",").mkString //4,3,2,1,0,5,3,2,1,0,
    println("rddDependencydoujjj:"+rddDependency)
    rddDependency=rddDependency.substring(0,rddDependency.length-1)//4,3,2,1,0,5,3,2,1,0
    return rddDependency
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

  /**Locate the line number from the log file according to RddId.
    * */
  def LocFromRDDId(logPath:String,RddId:String):String = {
val file = Source.fromFile(logPath, "utf-8")
    var intLineNum=9999999;
    var persistCodePath="";
   // val RddId="RDD[6]"
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
        //  println(persistCodePath)
        }
      }
    }
    return persistCodePath
  }

/**Read code which is need to be modified according to the line number.
  * */
  def ReadLine(sourceCodePath:String,lineNum:Integer):String = {
    val file=Source.fromFile(sourceCodePath,"utf-8")
    var intLineNumRead=235;
    val lineIterator = file.getLines.toList
    var s1 = new scala.collection.mutable.HashMap[String, String]
    var fixCode = lineIterator(intLineNumRead - 1) //index from 0,eg Index is 0 where Line is 1
    while (intLineNumRead > 0) {
      if (!fixCode.contains("=")) {
        fixCode = lineIterator(intLineNumRead - 1)
      }
      intLineNumRead = intLineNumRead - 1
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
   // println(s1)
    //s1.keys.foreach(println)
    var stringRDDName=s1.keySet.mkString(",")
   // println("stringRDDName:"+stringRDDName)
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
    val string2=string1.substring(0,string1.indexOf("=")-1)
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
    val rddName="processed,lastRDD"
    var rddName1 = rddName.split(",")
    for(c<-rddName.split(",")) {
      if (stringDeps.contains(c+".unpersist")) {
        unperExist = true
          println(c+".unpersist()") //lastRDD实现了unpersist方法
      }
    }
   // println(unperExist)
    file.close
    return unperExist
  }


}




