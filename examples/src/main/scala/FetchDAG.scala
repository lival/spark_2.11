import scala.io.Source
/**
  * Created by lh on 2017/12/26.
  */
object FetchDAG {//从log信息中获取DAG
    def main(args: Array[String]) {
    val file=Source.fromFile("G:/test.txt")
  // val file=Source.fromFile("D:/liGitRes/TestCase/Spark2661Test/logInfo.txt")
      val stringDeps=file.mkString;
      var rddDependency=""
      val pattern0="""((\+\-\(2+\))|(\+\-\(1+\)))""".r
       val n= pattern0.findAllIn(stringDeps).toArray.mkString//+-(2)  +-(1)
      val unStage0=pattern0.replaceAllIn(stringDeps, ",").mkString
      val pattern1="""(\(1\)|\(2\))""".r
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
      //println(s)
      val pattern6 = """\,+""".r
      val z= pattern6.replaceAllIn(s,",").mkString

      val pattern7 = """\];,""".r
      val x= pattern7.replaceAllIn(z,";").mkString

      val pattern8 = """(\[|\])""".r
      val r= pattern8.replaceAllIn(x,"").mkString

      val pattern9 ="""\;""".r
      rddDependency = pattern9.replaceAllIn(r, ",").mkString
      rddDependency=rddDependency.substring(1,rddDependency.length)
      println(rddDependency)
    }
}
