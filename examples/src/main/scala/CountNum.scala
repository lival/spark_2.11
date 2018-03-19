/**
  * Created by lh on 2017/12/27.
  */
object CountNum {
  def countWords(str: String) = {
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
  def main(args: Array[String]) {
    println(countWords("4,3,2,1,0;5,3,2,1,0,"))
    System.in.read()
  }
}
