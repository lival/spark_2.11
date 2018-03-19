import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
/**
  * Created by lh on 2017/5/16.
  */
object SPARK10182Test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("SPARK10182Test")
    val sc = new SparkContext(conf)
    val samples = Seq[LabeledPoint](
      LabeledPoint(1.0, Vectors.dense(1.0, 0.0)),
      LabeledPoint(1.0, Vectors.dense(0.0, 1.0)),
      LabeledPoint(0.0, Vectors.dense(1.0, 1.0)),
      LabeledPoint(0.0, Vectors.dense(0.0, 0.0))
    )

    val rdd = sc.parallelize(samples)

    for (i <- 0 until 1) {
      val model = {
        new LogisticRegressionWithLBFGS()
          .setNumClasses(2)
          .run(rdd)
          .clearThreshold()
      }
    }
  // System.in.read()
  }
}