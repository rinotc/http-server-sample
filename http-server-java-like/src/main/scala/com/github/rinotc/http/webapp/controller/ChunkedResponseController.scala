package com.github.rinotc.http.webapp.controller

import com.github.rinotc.http.message.{Request, Response}
import com.github.rinotc.http.webapp.Controller

import java.io.{ByteArrayOutputStream, IOException}
import java.util
import java.util.Arrays
import scala.util.{Failure, Success, Try}

class ChunkedResponseController extends Controller {
  override val path: String = "/chunked"

  val ChunkSize = 20

//  private val CRLF: Array[Byte] = Array[Byte](0x0d, 0x0a);
  private val LF: Array[Byte] = Array[Byte](0x0a)

  val basicController: Controller = new BasicHttpController

  /**
   * POST リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doPost(request: Request): Response = chunk(basicController.doPost(request))

  /**
   * GET リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doGet(request: Request): Response = basicController.doGet(request)

  private def chunk(response: Response): Response =
    val body = response.getBody

    val out = new ByteArrayOutputStream()
    val result = Try {
      var offset     = 0
      val bodyLength = body.length
      while (offset < bodyLength) {
        val chunk     = util.Arrays.copyOfRange(body, offset, offset + ChunkSize)
        val lengthHex = chunk.length.toHexString
        out.write(lengthHex.getBytes())
        out.write(LF)
        out.write(chunk)
        out.write(LF)
        offset += ChunkSize
      }
      out.write("0".getBytes())
      out.write(LF)
      out.write(LF)
    }
    (result: @unchecked) match {
      case Success(_) =>
        response.headers.remove("Content-Length")
        response.addHeaderField("Transfer-Encoding", "chunked")
        response.setBody(out.toByteArray)
        response
      case Failure(_: IOException) => response
    }
}
