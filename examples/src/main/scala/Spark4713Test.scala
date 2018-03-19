import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
object Spark4713Test {
  def main(args: Array[String]):Unit= {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("Spark4713Test")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    val df = spark.read.json("D:/spark/spark/examples/src/main/resources/people.json")
    // Displays the content of the DataFrame to stdout
   // df.show()
    df.createOrReplaceTempView("people")
    val sqlDF = spark.sql("SELECT * FROM people")
    sqlDF.show()
   //sqlDF.cache()
    sqlDF.unpersist()

  }
}
