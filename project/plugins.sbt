logLevel := Level.Info

resolvers += Resolver.bintrayIvyRepo("2m", "sbt-plugins")

addSbtPlugin("com.github.mwz" % "sbt-sonar" % "1.6.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.2.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.5.2")
addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.29")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.5")
addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "0.7.3")
addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.4.4")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.2.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.7-1")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.5")

resolvers += Resolver.bintrayIvyRepo("akka-fusion", "ivy")
addSbtPlugin("com.akka-fusion" % "fusion-sbt-plugin" % "2.0.4")
