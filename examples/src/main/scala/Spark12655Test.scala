import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._

/**
  * Created by lh on 2017/12/6.
  */
object Spark12655Test {
  def main(args: Array[String]):Unit= {
    val conf = new SparkConf().setMaster("local").setAppName("Spark12655Test")
    val sc = new SparkContext(conf)
    val vert = sc.parallelize(List((1L, 1), (2L, 2), (3L, 3)), 1)
    val edges = sc.parallelize(List(Edge[Long](1L, 2L), Edge[Long](1L, 3L)), 1)

    val g0 = Graph(vert, edges)
    val g = g0.partitionBy(PartitionStrategy.EdgePartition2D, 2)
    val cc = g.connectedComponents()
    println("before unpersist:"+sc.getPersistentRDDs.nonEmpty)
    //assert(sc.getPersistentRDDs.nonEmpty)
    cc.unpersist()
    g.unpersist()
    g0.unpersist()
    vert.unpersist()
    edges.unpersist()
    //assert(sc.getPersistentRDDs.isEmpty)
    println("after unpersist:"+sc.getPersistentRDDs.isEmpty)
   System.in.read()

  }
}
