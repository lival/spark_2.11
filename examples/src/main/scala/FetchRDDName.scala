/**
  * Created by lh on 2017/12/29.
  */
object FetchRDDName {
  def main(args: Array[String]):Unit= {
    //var string1 = " val rdd1 = sc.makeRDD(1 to 10).filter(_ % 4 == 0).map(Math.sqrt(_)).map(e1 => (e1.toInt, e1))"
    var string1 = "val processed = grouped.mapValues(x => (x._1.iterator, x._2.iterator))"
    // val string1="val stats: RDD[(BDM[Double], List[BDV[Double]])] = batch.mapPartitions { docs ="
    var string2=string1.substring(0,string1.indexOf("=")-1)
    if(string2.contains(":")){
      string2=string2.substring(0,string2.indexOf(":"))
    }
    val index=string2.lastIndexOf(" ")
    val rddName=string2.substring(index+1,string2.length)
    println(rddName)

  }
}
