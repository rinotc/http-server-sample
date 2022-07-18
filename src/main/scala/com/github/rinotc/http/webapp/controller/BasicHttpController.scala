package com.github.rinotc.http.webapp.controller

import com.github.rinotc.http.message.Status
import com.github.rinotc.http.message.{Request, Response}
import com.github.rinotc.http.server.SimpleHttpServer
import com.github.rinotc.http.webapp.Controller
import com.github.rinotc.http.util.StringExtension.fileExtension
import com.github.rinotc.http.util.EitherExtension.unwrap

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.Try

class BasicHttpController extends Controller {
  import BasicHttpController._

  override val path: String = "/"

  /**
   * POST リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doPost(request: Request): Response = {
    val body = new String(request.getBody, StandardCharsets.UTF_8)
    println(s"POST body: $body")
    new Response(protocolVersion, Status.NO_CONTENT)
  }

  /**
   * GET リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doGet(request: Request): Response = {
    var target = Paths.get(SimpleHttpServer.documentRoot, request.target).normalize()

    if (!target.startsWith(SimpleHttpServer.documentRoot)) {
      return new Response(protocolVersion, Status.BAD_REQUEST)
    }

    if (Files.isDirectory(target)) {
      target = target.resolve("index.html")
    }

    val response = Try {
      val response = new Response(protocolVersion, Status.OK)
      response.setBody(Files.readAllBytes(target))
      response.addHeaderField("Content-Length", response.getBody.length.toString)

      val ext         = target.getFileName.toString.fileExtension
      val contentType = SimpleHttpServer.extensionToContentType(ext)

      response.addHeaderField("Content-Type", contentType)
      response
    }.toEither.left.map { case _: IOException =>
      val response = new Response(protocolVersion, Status.NOT_FOUND)
      response.setBody(SimpleHttpServer.readErrorPage(Status.NOT_FOUND))
      response.addHeaderField("Content-Length", response.getBody.length.toString)
      response
    }

    response.unwrap
  }

}

object BasicHttpController {

  val protocolVersion = "HTTP/1.1"
}
