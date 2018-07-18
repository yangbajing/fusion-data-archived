package mass.http

import java.nio.file.Paths
import java.security.{KeyStore, SecureRandom}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.settings.ServerSettings
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.typesafe.scalalogging.StrictLogging
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import helloscala.common.Configuration
import helloscala.common.util.{PidFile, StringUtils, Utils}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/**
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-08.
 */
trait HSAkkaHttpServer extends BaseExceptionPF with BaseRejectionBuilder with StrictLogging {
  def actorSystem: ActorSystem

  def actorMaterializer: ActorMaterializer

  val hlServerValue: String

  def configuration: Configuration

  def routes: AbstractRoute

  def close(): Unit

  protected var bindingFuture: Future[ServerBinding] = _
  protected var httpsBindingFuture: Option[Future[ServerBinding]] = _
  protected var serverHost: String = _
  protected var serverPort: Int = _

  def shutdown(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val f = bindingFuture.flatMap { binding =>
      logger.info(s"Unbind: $binding")
      binding.unbind()
    }
    val fSsl = httpsBindingFuture.map(_.flatMap { binding =>
      logger.info(s"Unbind: $binding")
      binding.unbind()
    })
    try {
      Await.result(f, 60.seconds)
    } catch {
      case e: Exception =>
        logger.error("停止http服务错误", e)
    }
    try {
      if (fSsl.isDefined)
        Await.result(fSsl.get, 60.seconds)
    } catch {
      case e: Exception =>
        logger.error("停止https服务错误", e)
    }
    close()
  }

  def handleMapResponse(response: HttpResponse): HttpResponse = {
    val name = hlServerValue + ":" + serverHost + ":" + serverPort
    val headers = ServerHeader(name) +: response.headers
    response.copy(headers = headers)
  }

  def afterHttpBindingSuccess(binding: ServerBinding): Unit = {
    logger.info(s"Server online at http://${binding.localAddress.getHostName}:${binding.localAddress.getPort}/")
  }

  def afterHttpsBindingSuccess(binding: ServerBinding): Unit = {
    logger.info(s"Server online at https://${binding.localAddress.getHostName}:${binding.localAddress.getPort}/")
  }

  def afterHttpBindingFailure(cause: Throwable): Unit = {
    logger.error(s"Error starting the server ${cause.getMessage}", cause)
    close()
    System.exit(-1)
  }

  protected def generateHttps(): HttpsConnectionContext = {
    var hcc: HttpsConnectionContext = null
    try {
      val password = configuration.getString("ssl-config.password").toCharArray
      //    val password = "abcdef".toCharArray // do not store passwords in code, read them from somewhere safe!

      val ks = KeyStore.getInstance("PKCS12")
      val keystore = getClass.getClassLoader.getResourceAsStream("ssl-keys/server.p12")

      require(keystore != null, "Keystore required!")
      ks.load(keystore, password)

      val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
      keyManagerFactory.init(ks, password)

      val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
      tmf.init(ks)

      val sslContext: SSLContext = SSLContext.getInstance("TLS")
      sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
      hcc = ConnectionContext.https(sslContext, Some(AkkaSSLConfig(actorSystem)))
    } catch {
      case NonFatal(e) =>
        e.printStackTrace()
        System.exit(-1)
    }
    hcc
  }

  def startServerAwait(): Unit = {
    Await.result(startServer()._1, 60.seconds)
  }

  /**
   * 启动基于Akka HTTP的服务
   * @return
   */
  def startServer(): (Future[ServerBinding], Option[Future[ServerBinding]]) //=
  //    startServer(
  //      configuration.getString("server.host"),
  //      configuration.getInt("server.port"),
  //      configuration.get[Option[Int]]("server.https-port"))

  /**
   * 根据设置的host:绑定主机名和port:绑定网络端口 启动Akka HTTP服务
   */
  def startServer(host: String, port: Int, httpsPort: Option[Int]): (Future[ServerBinding], Option[Future[ServerBinding]]) = {
    implicit val system: ActorSystem = actorSystem
    implicit val mat: ActorMaterializer = actorMaterializer
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

    serverHost = host
    serverPort = port

    writePidfile()

    val flow: Flow[HttpRequest, HttpResponse, Any] =
      (handleRejections(rejectionHandler) &
        handleExceptions(exceptionHandler)) {
          routes.route
        }
    val handler = flow.map(handleMapResponse)

    bindingFuture = Http().bindAndHandle(
      handler,
      interface = host,
      port = port,
      settings = ServerSettings(actorSystem))

    bindingFuture.onComplete {
      case Success(binding) ⇒
        //setting the server binding for possible future uses in the client
        //        serverBinding.set(binding)
        //        binding.unbind()
        afterHttpBindingSuccess(binding)
      case Failure(cause) ⇒
        afterHttpBindingFailure(cause)
    }

    httpsBindingFuture = httpsPort.map { portSsl =>
      val f = Http().bindAndHandle(
        handler,
        interface = host,
        port = portSsl,
        connectionContext = generateHttps(),
        settings = ServerSettings(actorSystem))
      f.onComplete {
        case Success(binding) ⇒
          //setting the server binding for possible future uses in the client
          //        serverBinding.set(binding)
          //        binding.unbind()
          afterHttpsBindingSuccess(binding)
        case Failure(cause) ⇒
          afterHttpBindingFailure(cause)
      }
      f
    }

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = shutdown()
    })

    bindingFuture -> httpsBindingFuture
  }

  private def writePidfile(): Unit = {
    val pidfilePath = Option(System.getProperty("pidfile.path"))
      .orElse(configuration.get[Option[String]]("pidfile.path"))
      .orNull
    try {
      if (StringUtils.isNoneBlank(pidfilePath)) {
        PidFile(Utils.getPid).create(Paths.get(pidfilePath), deleteOnExit = true)
      } else {
        logger.warn("-Dpidfile.path 未设置，将不写入 .pid 文件。")
      }
    } catch {
      case NonFatal(e) =>
        logger.error(s"将进程ID写入文件：$pidfilePath 失败", e)
        System.exit(-1)
    }
  }

}
