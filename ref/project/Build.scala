import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ref"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "net.ech.jcas" % "core" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
    )

}
