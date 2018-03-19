/**
  * Created by lh on 2017/12/15.
  */
object testSome {
    def main(args: Array[String]) {
      val capitals = Map("France" -> "Paris", "Japan" -> "Tokyo")
      println("show(capitals.get( \"Japan\")) : " + show(capitals.get( "Japan")) )
      println("show(capitals.get( \"India\")) : " + show(capitals.get( "India")) )
    }
    def show(x: Option[String]) = x match {
      case Some(s) => s
      case None => "?"
    }

  /**  show(capitals.get( "Japan")) : Tokyo
show(capitals.get( "India")) : ?  */
}
