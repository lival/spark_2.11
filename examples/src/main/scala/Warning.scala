
/**
  * Created by lh on 2017/12/22.
  */
//在进行模式匹配的时候，有些时候需要确保所有的可能情况都被列出，此时常常会将case class的超类定义为sealed（密封的) case class
sealed abstract class Warning(val message: String) {
  def name: String = toString.takeWhile(_ != '(')
}
final object Warning{
  val All=Seq[Warning](
    checkCacheStrategy
  )
  val AllNames = All.map(_.name)

  val NameToWarning: Map[String, Warning] = All.map(w => w.name -> w).toMap
}
case object checkCacheStrategy extends
  Warning("This line should be used cache strategy to improve performance.")