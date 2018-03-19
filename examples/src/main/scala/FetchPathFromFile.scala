import java.net.URI
import java.io.File
/**
  * Created by lh on 2018/1/3.
  */
object FetchPathFromFile {
  def main(args: Array[String]): Unit = {
    var FileName=("Bagel.scala")
    val file = new File(FileName)
    val path = file.getAbsolutePath
    println("path:"+path.substring(0, path.lastIndexOf(File.separator)))
    //取得根目录路径
    var rootPath=getClass().getResource("/").getFile().toString();
    println("rootPath:"+rootPath)
    //当前目录路径
    var currentPath1=getClass().getResource(".").getFile().toString();
    println("currentPath1:"+currentPath1)
    var currentPath2=getClass().getResource("").getFile().toString();
    println("currentPath2:"+currentPath2)
    //当前目录的上级目录路径
   // var parentPath=getClass().getResource("../").getFile().toString();
   // println("parentPath:"+parentPath)
  }

}
