package com.github.rinotc.http.javaLike.webapp.controller

import com.github.rinotc.http.javaLike.message.{Request, Response, Status}
import com.github.rinotc.http.javaLike.server.SimpleHttpServer
import com.github.rinotc.http.javaLike.webapp.Controller
import com.github.rinotc.http.javaLike.util.StringExtension.fileExtension
import com.github.rinotc.http.javaLike.util.EitherExtension.unwrap

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.Try

class BasicHttpController extends Controller {

  private val ProtocolVersion = "HTTP/1.1"

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
    new Response(ProtocolVersion, Status.NO_CONTENT)
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
      return new Response(ProtocolVersion, Status.BAD_REQUEST)
    }

    if (Files.isDirectory(target)) {
      target = target.resolve("index.html")
    }

    val response = Try {
      val response = new Response(ProtocolVersion, Status.OK)
      response.setBody(Files.readAllBytes(target))
      response.addHeaderField("Content-Length", response.getBody.length.toString)

      val ext         = target.getFileName.toString.fileExtension
      val contentType = SimpleHttpServer.extensionToContentType(ext)

      response.addHeaderField("Content-Type", contentType)
      response
    }.toEither.left.map { case _: IOException =>
      val response = new Response(ProtocolVersion, Status.NOT_FOUND)
      response.setBody(SimpleHttpServer.readErrorPage(Status.NOT_FOUND))
      response.addHeaderField("Content-Length", response.getBody.length.toString)
      response
    }

    response.unwrap
  }

}
