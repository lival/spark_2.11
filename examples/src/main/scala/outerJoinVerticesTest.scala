import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
/**
  * Created by lh on 2017/12/14.
  */
object outerJoinVerticesTest {
  def main(args: Array[String]): Unit = {
    //设置运行环境
    val conf = new SparkConf().setAppName("outerJoinVerticesTest").setMaster("local")
    val sc = new SparkContext(conf)

    //创建点RDD
    val usersVertices: RDD[(VertexId, (String, String))] = sc.parallelize(Array(
      (1L, ("Spark", "scala")), (2L, ("Hadoop", "java")),
      (3L, ("Kafka", "scala")), (4L, ("Zookeeper", "Java ")))).persist()
    val userVertices=usersVertices;
    //创建边RDD
    val usersEdges: RDD[Edge[String]] = sc.parallelize(Array(
      Edge(2L, 1L, "study"), Edge(3L, 2L, "train"),
      Edge(1L, 2L, "exercise"), Edge(4L, 1L, "None")))

    val salaryVertices: RDD[(VertexId, (String, Long))] = sc.parallelize(Array(
      (1L, ("Spark", 30L)), (2L, ("Hadoop", 15L)),
      (3L, ("Kafka", 10L)), (5L, ("parameter server", 40L))
    ))
    val salaryEdges: RDD[Edge[String]] = sc.parallelize(Array(
      Edge(2L, 1L, "study"), Edge(3L, 2L, "train"),
      Edge(1L, 2L, "exercise"), Edge(5L, 1L, "None")))

    //构造Graph
    val graph = Graph(usersVertices, usersEdges)
    val graph1 = Graph(salaryVertices, salaryEdges)
    //outerJoinVertices操作,
    val joinGraph = graph.outerJoinVertices(graph1.vertices) { (id, attr, deps) =>
      deps match {
        case Some(deps) => deps
        case None => 0
      }
    }
  //  graph.vertices.collect().foreach(println)
    joinGraph.vertices.collect.foreach(println)
    /** (4,0)
      * (3,(Kafka,10))
      * (2,(hadoop,15)
      * (1,(spark,30)))
      * */
    if(sc.getPersistentRDDs.nonEmpty){
      println("Cached RDD is as follows:")
      sc.getPersistentRDDs.foreach( r => println( r._2.toString))
    }
    /** Cached RDD is as follows:
EdgeRDD MapPartitionsRDD[14] at mapPartitions at EdgeRDDImpl.scala:119
VertexRDD ZippedPartitionsRDD2[28] at zipPartitions at VertexRDDImpl.scala:156
VertexRDD, VertexRDD ZippedPartitionsRDD2[11] at zipPartitions at VertexRDD.scala:322
EdgeRDD MapPartitionsRDD[26] at mapPartitions at EdgeRDDImpl.scala:119
VertexRDD, VertexRDD ZippedPartitionsRDD2[23] at zipPartitions at VertexRDD.scala:322
EdgeRDD MapPartitionsRDD[30] at mapPartitions at EdgeRDDImpl.scala:119

      EdgeRDD MapPartitionsRDD[14] at mapPartitions at EdgeRDDImpl.scala:119
VertexRDD ZippedPartitionsRDD2[28] at zipPartitions at VertexRDDImpl.scala:156
VertexRDD, VertexRDD ZippedPartitionsRDD2[11] at zipPartitions at VertexRDD.scala:322
EdgeRDD MapPartitionsRDD[26] at mapPartitions at EdgeRDDImpl.scala:119
VertexRDD, VertexRDD ZippedPartitionsRDD2[23] at zipPartitions at VertexRDD.scala:322
EdgeRDD MapPartitionsRDD[30] at mapPartitions at EdgeRDDImpl.scala:119
      * */
    sc.stop()
  }
}
