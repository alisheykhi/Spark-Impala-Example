# Spark-Impala-Example
This project is an example for reading data from Impala (using impala for transformation) as a Spark DataFrame and writing objects from Spark into oracle database using JDBC.

## Version Compatibility

Scala| Spark|Impala JDBC|Oracle JDBC|sbt     
--- | --- | ---| ---| ---
2.11.12| 2.4.0 | 2.6.23.1028 | odbc8 | 1.3.13

## Build from Source
```bash 
$ sbt assembly
```
## Run 

in order to run this application in kerberos enabled environment use the following command. 
you have to create your jaas.config file based on your production.

```bash
spark-submit
 --deploy-mode cluster
 --files "spark_jaas.conf#spark_jaas.conf,your_keytabfile..keytab#your_keytabfile..keytab"
 --driver-java-options "-Djava.security.auth.login.config=./spark_jaas.conf"
 --conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=./spark_jaas.conf"
 --conf spark.yarn.submit.waitAppCompletion=false
 --driver-memory 16G
 --name SPARK_IMPALA
 --files config.ini,log4j.properties,spark_jaas.conf,your_keytabfile..keytab
 path_to_your_jar_file.jar path_to_log4j.properties path_to_config.ini

```

