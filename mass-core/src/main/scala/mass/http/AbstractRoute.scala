package mass.http

import java.io.File
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path, Paths}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.CacheDirectives.{`no-cache`, `no-store`}
import akka.http.scaladsl.server.PathMatcher.{Matched, Unmatched}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.util.Tuple
import akka.http.scaladsl.unmarshalling.{
  FromRequestUnmarshaller,
  FromStringUnmarshaller,
  Unmarshaller
}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import helloscala.common.data.ApiResult
import helloscala.common.exception.{
  HSBadRequestException,
  HSException,
  HSNotFoundException
}
import helloscala.common.page.{Page, PageInput}
import helloscala.common.types.{AsInt, ObjectId}
import helloscala.common.util.TimeUtils

import scala.annotation.tailrec
import scala.collection.immutable
import scala.concurrent.Future

trait IAppIdKey {
  def appId: String

  def appKey: String
}

trait AbstractRoute extends Directives {
  def route: Route

  def createTempFileFunc(dir: Path = Paths.get("/tmp"),
                         prefix: String = "mass-",
                         suffix: String = ".tmp"): FileInfo => File =
    fileInfo => Files.createTempFile(dir, prefix, suffix).toFile

  implicit class ContentTypeRich(contentType: ContentType) {
    def charset: Charset =
      contentType.charsetOption
        .map(_.nioCharset())
        .getOrElse(StandardCharsets.UTF_8)
  }

  implicit def objectIdFromStringUnmarshaller
    : FromStringUnmarshaller[ObjectId] =
    Unmarshaller.strict[String, ObjectId] {
      case str if ObjectId.isValid(str) => ObjectId.apply(str)
      case str                          => throw HSBadRequestException(s"$str 不是有效的ObjectId字符串")
    }

  implicit def localDateFromStringUnmarshaller
    : FromStringUnmarshaller[LocalDate] =
    Unmarshaller.strict[String, LocalDate] { str =>
      LocalDate.parse(str, TimeUtils.formatterDate)
    }

  implicit def localTimeFromStringUnmarshaller
    : FromStringUnmarshaller[LocalTime] =
    Unmarshaller.strict[String, LocalTime] { str =>
      LocalTime.parse(str, TimeUtils.formatterTime)
    }

  implicit def localDateTimeFromStringUnmarshaller
    : FromStringUnmarshaller[LocalDateTime] =
    Unmarshaller.strict[String, LocalDateTime] { str =>
      LocalDateTime.parse(str, TimeUtils.formatterDateTime)
    }

  def ObjectIdPath: PathMatcher1[ObjectId] =
    PathMatcher("""[\da-fA-F]{24}""".r) flatMap { string =>
      try ObjectId.parse(string).toOption
      catch {
        case _: IllegalArgumentException ⇒ None
      }
    }

  def ObjectIdSegment: PathMatcher1[String] =
    PathMatcher("""[\da-fA-F]{24}""".r) flatMap { string =>
      Some(string).filter(ObjectId.isValid)
    }

  def hsLogRequest(logger: com.typesafe.scalalogging.Logger): Directive0 =
    mapRequest { req =>
      def entity = req.entity match {
        case HttpEntity.Empty => ""
        case _                => "\n" + req.entity
      }

      logger.debug(s"""
         |method: ${req.method.value}
         |uri: ${req.uri}
         |search: ${req.uri.rawQueryString}
         |header: ${req.headers.mkString("\n        ")}$entity""".stripMargin)
      req
    }

  def extractPageInput: Directive1[PageInput] = extract { ctx =>
    val query = ctx.request.uri.query()
    val page = query
      .get("page")
      .flatMap(AsInt.unapply)
      .getOrElse(Page.DEFAULT_PAGE)
    val size =
      query.get("size").flatMap(AsInt.unapply).getOrElse(Page.DEFAULT_SIZE)
    PageInput(page, size, query.filterNot {
      case (name, _) => name == "page" || name == "size"
    }.toMap)
  }

  def notPathPrefixTest[L](pm: PathMatcher[L]): Directive0 =
    rawNotPathPrefixTest(Slash ~ pm)

  def rawNotPathPrefixTest[L](pm: PathMatcher[L]): Directive0 = {
    implicit val LIsTuple: Tuple[L] = pm.ev
    extract(ctx => pm(ctx.unmatchedPath)).flatMap {
      case Matched(v, values) ⇒
        println(s"notPathPrefixTest v: $v, values: $values")
        reject
      case Unmatched ⇒ pass
    }
  }

  def setNoCache: Directive0 =
    mapResponseHeaders(
      h =>
        h ++ List(headers.`Cache-Control`(`no-store`, `no-cache`),
                  headers.RawHeader("Pragma", "no-cache")))

  def completeOk: Route = complete(HttpEntity.Empty)

  def completeNotImplemented: Route = complete(StatusCodes.NotImplemented)

  def pathGet[L](pm: PathMatcher[L]): Directive[L] = path(pm) & get

  def pathPost[L](pm: PathMatcher[L]): Directive[L] = path(pm) & post

  def pathPut[L](pm: PathMatcher[L]): Directive[L] = path(pm) & put

  def pathDelete[L](pm: PathMatcher[L]): Directive[L] = path(pm) & delete

  def putEntity[T](um: FromRequestUnmarshaller[T]): Directive1[T] =
    put & entity(um)

  def postEntity[T](um: FromRequestUnmarshaller[T]): Directive1[T] =
    post & entity(um)

  def completionStageComplete(
      future: java.util.concurrent.CompletionStage[AnyRef],
      needContainer: Boolean = false,
      successCode: StatusCode = StatusCodes.OK): Route = {
    import scala.compat.java8.FutureConverters._
    val f: AnyRef => Route = objectComplete(_, needContainer, successCode)
    onSuccess(future.toScala).apply(f)
  }

  def futureComplete(
      future: Future[AnyRef],
      needContainer: Boolean = false,
      successCode: StatusCode = StatusCodes.OK
  ): Route = {
    val f: AnyRef => Route = objectComplete(_, needContainer, successCode)
    onSuccess(future).apply(f)
  }

  @tailrec
  final def objectComplete(
      obj: Any,
      needContainer: Boolean = false,
      successCode: StatusCode = StatusCodes.OK
  ): Route = {
    import JacksonSupport._
    obj match {
      case Right(result) =>
        objectComplete(result, needContainer, successCode)

      case Left(e: HSException) =>
        objectComplete(e, needContainer, successCode)

      case Some(result) =>
        objectComplete(result, needContainer, successCode)

      case None =>
        complete(HSNotFoundException("数据不存在"))

      case response: HttpResponse =>
        complete(response)

      case responseEntity: ResponseEntity =>
        complete(HttpResponse(successCode, entity = responseEntity))

      case result: ApiResult =>
        val status =
          if (result.getErrCode == null || result.getErrCode.equals(0))
            StatusCodes.OK
          else if (successCode != StatusCodes.OK) successCode
          else
            StatusCodes.getForKey(result.getErrCode).getOrElse(StatusCodes.OK)
        complete((status, result))

      case status: StatusCode =>
        complete(status)

      case result =>
        val resp = if (needContainer) ApiResult.success(result) else result
        complete((successCode, resp))
    }
  }

  def eitherComplete[T](either: Either[HSException, T]): Route = {
    either match {
      case Right(result) =>
        objectComplete(result)
      case Left(e) =>
        objectComplete(e)
    }
  }

  def multiUploadedFile: Directive1[immutable.Seq[(FileInfo, Path)]] =
    entity(as[Multipart.FormData])
      .flatMap { formData ⇒
        extractRequestContext.flatMap { ctx ⇒
          import ctx.{executionContext, materializer}

          val multiPartF = formData.parts
            .map { part =>
              val destination = Files.createTempFile("akka-http-upload", ".tmp")
              val uploadedF: Future[(FileInfo, Path)] =
                part.entity.dataBytes
                  .runWith(FileIO.toPath(destination))
                  .map(
                    _ =>
                      (FileInfo(part.name,
                                part.filename.get,
                                part.entity.contentType),
                       destination))
              uploadedF
            }
            .runWith(Sink.seq)
            .flatMap(list => Future.sequence(list))

          onSuccess(multiPartF)
        }
      }
      .flatMap {
        case Nil  => reject(ValidationRejection("没有任何上传文件"))
        case list => provide(list)
      }

  def multiFileUpload
    : Directive1[immutable.Seq[(FileInfo, Source[ByteString, Any])]] =
    entity(as[Multipart.FormData])
      .flatMap { formData ⇒
        extractRequestContext.flatMap { ctx ⇒
          import ctx.materializer

          val multiPartF = formData.parts
            .map(part ⇒
              (FileInfo(part.name, part.filename.get, part.entity.contentType),
               part.entity.dataBytes))
            .runWith(Sink.seq)

          onSuccess(multiPartF)
        }
      }
      .flatMap {
        case Nil  => reject(ValidationRejection("没有任何上传文件"))
        case list => provide(list)
      }

  /**
    * REST API 转发代理
    *
    * @param uri 要转发的地址
    * @param sourceQueue AkkaHTTP 源连接队列
    * @return
    */
  def restApiProxy(uri: Uri)(implicit sourceQueue: AkkaHttpSourceQueue): Route =
    extractRequestContext { ctx =>
      val req = ctx.request
      val request = req.copy(uri = uri.withQuery(req.uri.query()))
      val future = HttpUtils.hostRequest(request)(sourceQueue.httpSourceQueue,
                                                  ctx.executionContext)
      onSuccess(future) { response =>
        complete(response)
      }
    }

  /**
    * * REST API 转发代理
    *
    * @param uri 要转发的地址
    * @param appIdKeyTokenConfig 接口账号参数
    * @param sourceQueue AkkaHTTP 源连接队列
    * @return
    */
  def restApiTokenProxy(uri: Uri)(implicit
                                  appIdKeyTokenConfig: IAppIdKey,
                                  sourceQueue: AkkaHttpSourceQueue): Route = {
    extractRequestContext { ctx =>
      val req = ctx.request
      val request =
        HttpUtils.applyApiToken(req.copy(uri = uri.withQuery(req.uri.query())),
                                appIdKeyTokenConfig.appId,
                                appIdKeyTokenConfig.appKey)
      val future = HttpUtils.hostRequest(request)(sourceQueue.httpSourceQueue,
                                                  ctx.executionContext)
      onSuccess(future) { response =>
        complete(response)
      }
    }
  }

}
