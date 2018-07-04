package mass.server.route

import java.io.File

import akka.http.javadsl.server.directives.FileInfo
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive, Directives, PathMatcher, StandardRoute}
import mass.core.json.Json4sFormats
import org.json4s.Formats

trait AbstractRoute extends Directives {
  implicit def jsonFormats: Formats = Json4sFormats

  def pathGet[L](p: PathMatcher[L]): Directive[L] = path(p) & get

  def pathPost[L](p: PathMatcher[L]): Directive[L] = path(p) & post

  def pathPut[L](p: PathMatcher[L]): Directive[L] = path(p) & put

  def pathDelete[L](p: PathMatcher[L]): Directive[L] = path(p) & delete

  def completeOk: StandardRoute = complete(StatusCodes.OK)

  def completeNotImplemented: StandardRoute = complete(StatusCodes.NotImplemented)

  val createTempFileFunc: FileInfo => File = fileInfo => File.createTempFile("mass-", ".tmp")
}
