/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.util

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.security.MessageDigest

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Sink}

import scala.concurrent.Future

object MessageDigestAlgorithms {
  /**
   * The MD5 message digest algorithm defined in RFC 1321.
   */
  val MD5 = "MD5"

  /**
   * The SHA-1 hash algorithm defined in the FIPS PUB 180-2.
   */
  val SHA_1 = "SHA-1"

  /**
   * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
   */
  val SHA_256 = "SHA-256"

  /**
   * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
   */
  val SHA_512 = "SHA-512"
}

object DigestUtils {
  val DEFAULT_BYTE_BUFFER_SIZE: Int = 8192

  val DEFAULT_FILE_BYTE_BUFFER_SIZE: Int = 1024 * 1024

  def digestMD5(): MessageDigest = {
    MessageDigest.getInstance(MessageDigestAlgorithms.MD5)
  }

  def digestSha1(): MessageDigest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1)

  def digestSha256(): MessageDigest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256)

  def digestSha512(): MessageDigest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_512)

  def _sha(input: Array[Byte], md: MessageDigest): Array[Byte] = {
    md.update(input)
    md.digest()
  }

  def _sha(path: Path, md: MessageDigest): Array[Byte] =
    _sha(path, md, ByteBuffer.allocateDirect(DEFAULT_BYTE_BUFFER_SIZE))

  def _sha(
      path: Path,
      md: MessageDigest,
      buf: ByteBuffer
  ): Array[Byte] = {
    val channel = Files.newByteChannel(path)
    try {
      var rsize = 0
      do {
        rsize = channel.read(buf)
        if (rsize > 0) {
          md.update(buf)
          buf.clear()
        }
      } while (rsize >= -1)
      md.digest()
    } finally {
      channel.close()
    }
  }

  def md5(data: Array[Byte]): Array[Byte] = _sha(data, digestMD5())

  def md5Hex(data: Array[Byte]): String = StringUtils.hex2Str(md5(data))

  def md5Hex(data: String): String = md5Hex(data.getBytes(StandardCharsets.UTF_8))

  def sha1(data: Array[Byte]): Array[Byte] = _sha(data, digestSha1())

  def sha1Hex(data: Array[Byte]): String = StringUtils.hex2Str(sha1(data))

  def sha1Hex(data: String): String = sha1Hex(data.getBytes(StandardCharsets.UTF_8))

  def sha1(path: Path): Array[Byte] = _sha(path, digestSha1(), ByteBuffer.allocateDirect(DEFAULT_FILE_BYTE_BUFFER_SIZE))

  def sha1Hex(path: Path): String = StringUtils.hex2Str(sha1(path))

  def sha256(data: Array[Byte]): Array[Byte] = _sha(data, digestSha256())

  def sha256Hex(data: Array[Byte]): String = StringUtils.hex2Str(sha256(data))

  def sha256Hex(data: String): String = sha256Hex(data.getBytes(StandardCharsets.UTF_8))

  def sha256(path: Path): Array[Byte] = _sha(path, digestSha256(), ByteBuffer.allocateDirect(DEFAULT_FILE_BYTE_BUFFER_SIZE))

  def sha256Hex(path: Path): String = StringUtils.hex2Str(sha256(path))

  def sha512(data: Array[Byte]): Array[Byte] = _sha(data, digestSha512())

  def sha512Hex(data: Array[Byte]): String = StringUtils.hex2Str(sha512(data))

  def sha512Hex(data: String): String = sha512Hex(data.getBytes(StandardCharsets.UTF_8))

  def sha512(path: Path): Array[Byte] = _sha(path, digestSha512(), ByteBuffer.allocateDirect(DEFAULT_FILE_BYTE_BUFFER_SIZE))

  def sha512Hex(path: Path): String = StringUtils.hex2Str(sha512(path))

  def reactiveSha256Hex(path: Path)(implicit mat: ActorMaterializer): Future[String] = {
    import mat.executionContext
    reactiveSha256(path).map(bytes => StringUtils.hex2Str(bytes))
  }

  def reactiveSha256(path: Path)(implicit mat: ActorMaterializer): Future[Array[Byte]] = {
    import mat.executionContext
    val md = digestSha256()
    FileIO
      .fromPath(path)
      .map(bytes => md.update(bytes.asByteBuffer))
      .runWith(Sink.ignore)
      .map(_ => md.digest())
  }
}
