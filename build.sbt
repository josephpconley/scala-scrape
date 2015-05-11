name := "scrape"

version := "1.0"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "net.sourceforge.htmlunit" % "htmlunit" % "2.13",
  "org.apache.httpcomponents" % "httpclient" % "4.3.1",
  "com.itextpdf" % "itextpdf" % "5.4.5",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.2",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"
)

//send output to toolbox app
//artifactPath in Compile in packageBin <<= new File("C")
