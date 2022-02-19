
import org.apache.spark.sql.SparkSession
import java.util.{Properties}

import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects}

import scala.collection.JavaConversions._


object SparkImpalaExample extends App with SparkLogger {

        val log4Path:String=args(0)
        val iniFilePath:String=args(1)

        LogHandler.apply(log4Path)

        val configHandler = new ConfigHandler(iniFilePath).apply()

        val sparkSession: SparkSession = new SparkSessionHandler(
        configHandler.sparkSessionSection
        ).getSparkSession

        val prop = new Properties()

        for( optionKey <- configHandler.sparkJDBCSection.keySet().filter( _ matches "prop.*" )) {
        prop.setProperty(
                optionKey.split("\\.")(1),configHandler.sparkJDBCSection.get(optionKey,classOf[String])
        )
        }

        //define required properties for kerberos enabled environment

        System.setProperty("java.security.auth.login.config","jaas.conf")  // consider jaas.conf file in spark-submit
        System.setProperty("sun.security.jgss.debug","true")
        System.setProperty("javax.security.auth.useSubjectCredsOnly","false")

        /* Update JDBC dialect for impala db
         * we have to override canHandle and quoteIdentifier functions in JdbcDialect class
         * */
        val ImpalaDialect = new JdbcDialect {
        override def canHandle(url: String):Boolean = url.startsWith("jdbc:impala")||url.contains("impala")
        override def quoteIdentifier(colName: String): String = {s"$colName"}
        }

        // register dialect in spark session
        JdbcDialects.registerDialect(ImpalaDialect)

        val query =
        """
        |select *
        | from impalaDB.table
        |""".stripMargin

        val df =
        sparkSession
        .sqlContext
        .read
        .jdbc(
                configHandler.sparkJDBCSection.get("url"),
                query,
                prop
        )


        df
          // do your transformation
        .withColumn("month_key",lit(202011))
          // output columns defined in config file
        .select(configHandler.writeSection.get("target.table.columns").split(",").map(col):_*)
        .write
        .format("jdbc")
        .options(Map(
        "url"->configHandler.writeSection.get("target.db.jdbc.url"),
        "driver"->configHandler.writeSection.get("driver"),
        "dbtable"->configHandler.writeSection.get("target.table.name"),
        "user"->configHandler.writeSection.get("target.db.user"),
        "password"->configHandler.writeSection.get("target.db.password")))
        .mode("append")
        .save()

        // unregister dialect
        JdbcDialects.unregisterDialect(ImpalaDialect)

}
