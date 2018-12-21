package example.motivating

import java.nio.file.{Files, Paths}

import mass.Global
import mass.extension.MassSystem
import mass.job.repository.JobRepo

import scala.concurrent.duration.Duration

object Demo2 extends App {
//  Duration.Undefined
//  println(Files.createTempFile(Paths.get("/tmp/mass-temp"), "mass-", ".tmp"))
//
//  val mass = MassSystem(Global.system)
//  val ss = mass.sqlManager.stream(JobRepo.listJob(null))
//
  val l = Map(1 -> 2, 3 -> 4, 5 -> 6).valuesIterator.collect { case 2 => 2 }

  val a = l
  println(a)
}
