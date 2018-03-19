
/**
  * Created by lh on 2017/12/22.
  */
abstract class Check

case class ErrorProne(levelType:String,categoryType:String,message:String) extends Check //case class 会自动生成apply方法，从而省去new操作
case class Performance(levelType:String,categoryType:String,message:String) extends Check
object CaseClassDemo{
  def main(args: Array[String]): Unit = {
    val p:Check=ErrorProne("Warning","ErrorProne","WarningWarningWarningWarning")
    val q:Check=Performance("Info","Performance","InfoInfoInfoInfo")
   p  match {
      case ErrorProne(levelType,categoryType,message)=>println(levelType+":"+"Unpersist operation should be used.---"+categoryType+";"+message)
      case Performance(levelType,categoryType,message)=>println(levelType+":"+"Perisit operation should be used.---"+categoryType+";"+message)
    }
  }
}
