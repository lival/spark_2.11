package org.apache.spark.mllib.impl
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._


/**
  * Created by lh on 2017/12/6.
  */
object Spark17559Test {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("Spark17559Test")
    val sc = new SparkContext(conf)
    val graphCheckpointer = new PeriodicGraphCheckpointer(2, sc)
    val users = sc.textFile("D:/liGitRes/SparkBranch/spark/data/graphx/users.txt")
      .map(line => line.split(",")).map(parts => (parts.head.toLong, parts.tail))
    val followerGraph = GraphLoader.edgeListFile(sc, "D:/liGitRes/SparkBranch/spark/data/graphx/followers.txt")
    val graph = followerGraph.outerJoinVertices(users) {
      case (uid, deg, Some(attrList)) => attrList case (uid, deg, None) => Array.empty[String] }
    //graphCheckpointer.update(graph)
  }
}
