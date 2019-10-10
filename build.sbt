name := "KeywordCompareProcess"

version := "0.1"

scalaVersion := "2.13.0"


libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/mysql/mysql-connector-java
  "mysql" % "mysql-connector-java" % "8.0.16"

)
// https://mvnrepository.com/artifact/org.apache.poi/poi
libraryDependencies += "org.apache.poi" % "poi" % "4.1.0"
// https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "4.1.0"
