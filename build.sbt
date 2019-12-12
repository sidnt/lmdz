import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.0.1"

lazy val lmdz = (project in file("."))
  .settings(
    name := "lmdz",
    libraryDependencies ++= Seq(
      zio,
      lmdb4j
    )
  )

lazy val lmdzclient0 = (project in file("lmdzclient0"))
  .dependsOn(lmdz)
  .settings(
    name := "lmdzclient0",
    libraryDependencies ++= Seq(
      zio
    )
  )
