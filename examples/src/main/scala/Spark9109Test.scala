import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory
import org.apache.spark.graphx.util.GraphGenerators
/**
  * Created by lh on 2017/11/14.
  */
object Spark9109Test {
  def main(args: Array[String]):Unit={
    val conf = new SparkConf().setMaster("local").setAppName("Spark9109Test")
    val sc = new SparkContext(conf)

    val users: RDD[(VertexId, (String, String))] =
      sc.parallelize(Array((3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
        (5L, ("franklin", "prof")), (2L, ("istoica", "prof"))))
    // Create an RDD for edges
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(Edge(3L, 7L, "collab"),    Edge(5L, 3L, "advisor"),
        Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi")))
    // Define a default user in case there are relationship with missing user
    val defaultUser = ("John Doe", "Missing")
    // Build the initial Graph
    val graph = Graph(users, relationships, defaultUser)
    graph.cache().numEdges

   graph.unpersist()
    println("EdgesNum:"+graph.cache().numEdges)
    //persistentRdds这个函数进行跟踪所有进行缓存的函数，它就用一个hashmap进行存储的，形式是：[Int, RDD[_]]
    sc.getPersistentRDDs.foreach( r => println( "hhhhhh:"+r._2.toString))
    /**
      EdgeRDD MapPartitionsRDD[14] at mapPartitions at EdgeRDDImpl.scala:119
VertexRDD ZippedPartitionsRDD2[28] at zipPartitions at VertexRDDImpl.scala:156
VertexRDD, VertexRDD ZippedPartitionsRDD2[11] at zipPartitions at VertexRDD.scala:322
EdgeRDD MapPartitionsRDD[26] at mapPartitions at EdgeRDDImpl.scala:119
VertexRDD, VertexRDD ZippedPartitionsRDD2[23] at zipPartitions at VertexRDD.scala:322
EdgeRDD MapPartitionsRDD[30] at mapPartitions at EdgeRDDImpl.scala:119
      */
    graph.unpersist()
    println("EdgesNum:"+graph.cache().numEdges)
    System.in.read()
  }
}
