import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._;

object Spark15870Test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setAppName("Spark15870Test")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val peopleFile = sc.textFile("D:/spark/examples/src/main/resources/people.txt")
    // The schema is encoded in a string
    val schemaString = "name age"
    // Generate the schema based on the string of schema
    val schema =
      StructType(
        schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))
    // Convert records of the RDD (people) to Rows.
    val rowRDD = peopleFile.map(_.split(",")).map(p => Row(p(0), p(1).trim))
    // Apply the schema to the RDD.
    val peopleDataFrame = sqlContext.createDataFrame(rowRDD, schema)
    // Register the DataFrames as a table.
    peopleDataFrame.registerTempTable("people")
    sqlContext.cacheTable("people")
    // SQL statements can be run by using the sql methods provided by sqlContext.
    val results = sqlContext.sql("SELECT * FROM people")
    println("results:" +results)
   // results.show()
    /**[Michael,29]
       [Andy,30]
       [Justin,19]
      * */
    sqlContext.uncacheTable("people")
    results.collect().foreach(println)
   // println("查看查询的整个运行计划")
     results.queryExecution
    println("查看查询的Unresolved LogicalPlan:")
    println(results.queryExecution.logical)
    println("查看查询的Analyzed LogicalPlan:")
    println(results.queryExecution.analyzed)
    println("查看优化后的LogicalPlan:")
    println(results.queryExecution.optimizedPlan)
    println("查看物理计划:")
    println(results.queryExecution.sparkPlan)
    sc.stop()
  }
}