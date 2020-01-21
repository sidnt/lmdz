import sbt._

object Dependencies {

  object Versions {
    val zioV = "1.0.0-RC17"
    val lmdb4jV = "0.7.0"
    val zioNioV = "1.0.0-RC2"
  }; import Versions._

  lazy val zio = "dev.zio" %% "zio" % zioV
  lazy val lmdb4j = "org.lmdbjava" % "lmdbjava" % lmdb4jV
  lazy val zioNio = "dev.zio" %% "zio-nio" % zioNioV

}
