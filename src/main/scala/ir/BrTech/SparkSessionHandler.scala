import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.ini4j.Profile

import scala.collection.JavaConversions._

class SparkSessionHandler(sparkSessionSection: Profile.Section )  {
  def getSparkSession: SparkSession = {
    val conf: SparkConf = new SparkConf()
    for (optionKey <- sparkSessionSection.keySet()) {
      conf.set(optionKey, sparkSessionSection.get(optionKey, classOf[String]))
    } //  Create sparkSession
    val sparkSession: SparkSession = sparkSessionSection.get("spark.master") match {
      case "local" => SparkSession.builder().config(conf).getOrCreate()
      case _ => SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    }
    //  Set log level
    sparkSession.sparkContext.setLogLevel("ERROR")
    sparkSession
  }
}
