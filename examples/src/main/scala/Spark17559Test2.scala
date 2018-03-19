package org.apache.spark.mllib.impl
import org.apache.spark.graphx._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}



/**
  * Created by lh on 2017/12/6.
  */
object Spark17559Test2 {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("Spark17559Test2")
    val sc = new SparkContext(conf)
    val graphCheckpointer = new PeriodicGraphCheckpointer(2, sc)
    val users = sc.textFile("D:/spark/data/graphx/users.txt")
      .map(line => line.split(",")).map(parts => (parts.head.toLong, parts.tail))
    val followerGraph = GraphLoader.edgeListFile(sc, "D:/liGitRes/SparkBranch/spark/data/graphx/followers.txt")
    val edges = Seq(
      Edge[Double](0, 1, 0),
      Edge[Double](1, 2, 0),
      Edge[Double](2, 3, 0),
      Edge[Double](3, 4, 0))

    def createGraph(sc: SparkContext): Graph[Double, Double] = {
      Graph.fromEdges[Double, Double](
        sc.parallelize(edges), 0, StorageLevel.MEMORY_ONLY_SER, StorageLevel.MEMORY_ONLY_SER)
    }
    val graph1 = createGraph(sc)

    val checkpointer =
      new PeriodicGraphCheckpointer[Double, Double](10, graph1.vertices.sparkContext)
    val graph = createGraph(sc)
    checkpointer.update(graph)

    /*val graph = followerGraph.outerJoinVertices(users) {
      case (uid, deg, Some(attrList)) => {
        attrList.map(a=>a.toDouble)
      }
      case (uid, deg, None) => Array.empty[Double]
    }
    //Dataset type, such as RDD[Double]
    val rdd=sc.makeRDD(Array(1L,2L,3L,4L))
    graphCheckpointer.update(graph)*/
  }
}
