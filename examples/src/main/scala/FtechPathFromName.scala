import java.io.File

/**
  * Created by imdg on 2018/1/9.
  */
object FtechPathFromName {



  //遍历某目录下所有的文件和子文件
  def main(args:Array[String]):Unit = {
    var derPath="D:\\spark\\TestCase\\"
    for(d <- subDir(new File(derPath))){
     // println(d)
      if(d.toString.contains("Bagel.scala"))
        println(d)
    }
  }

  def subDir(dir:File):Iterator[File] ={
    val dirs = dir.listFiles().filter(_.isDirectory())
    val files = dir.listFiles().filter(_.isFile())
    files.toIterator ++ dirs.toIterator.flatMap(subDir _)
  }

}
