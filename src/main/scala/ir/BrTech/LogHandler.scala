import org.apache.log4j.{Level, Logger, PropertyConfigurator}

object LogHandler {

  def apply(log4Path: String): Unit = {
    PropertyConfigurator.configure(log4Path)
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.INFO)
  }

}
