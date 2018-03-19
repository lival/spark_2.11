/**
  * Created by lh on 2017/12/28.
  */
object UnpersistRddId {
    def main(args: Array[String]):Unit= {
      val string1="4,3,2,1,0,5,3,2,1,0"
      val string2="3,2,1,0"
      val index=string1.lastIndexOf(string2)-1 //12
      val string3=string1.substring(0,index)
      println(string3)
      val index1=string3.lastIndexOf(",")
      val string4=string3.substring(index1+1,string3.length)
      println(string4)
  }
}
