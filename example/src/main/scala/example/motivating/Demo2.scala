package example.motivating

import java.nio.file.{Files, Paths}

object Demo2 extends App {
  println(Files.createTempFile(Paths.get("/tmp/mass-temp"), "mass-", ".tmp"))
}
