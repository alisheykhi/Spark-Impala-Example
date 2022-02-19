import java.io.{File, FileNotFoundException, IOException}

import org.ini4j.{Profile, Wini}
import SparkImpalaExample.error

class ConfigHandler(initFilePath: String) {

  //Read all section from config file : Common Sections
  var sparkSessionSection: Profile.Section = _
  var sparkJDBCSection: Profile.Section = _
  var sparkSourceTableSection: Profile.Section = _
  var writeSection: Profile.Section = _


  def apply() = {
    val ini: Wini = {
      try {
        new Wini(new File(initFilePath))
      } catch {
        case x: FileNotFoundException =>
          println("Exception: File missing")
          error("Exception - ", x)
          null
        case x: IOException =>
          println("Input/output Exception")
          error("Exception - ", x)
          null
      }
    }
    //Read all section from config file : Common Sections
    sparkSessionSection = ini.get("SparkSession")
    sparkSourceTableSection = ini.get("SourceTableQuery")
    sparkJDBCSection = ini.get("JDBC")
    writeSection = ini.get("DestTable")
    this
  }

}
