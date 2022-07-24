package com.github.rinotc.http.webapp.controller

import com.github.rinotc.http.message.{Request, Response}
import com.github.rinotc.http.webapp.Controller

import com.github.rinotc.http.message.Status

/**
 * リクエストボディをそのまま返すコントローラ
 */
class EchoController extends Controller {
  override val path = "/webapp/echo"

  private val ProtocolVersion = "HTTP/1.1"

  /**
   * POST リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doPost(request: Request): Response = echo(request)

  /**
   * GET リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doGet(request: Request): Response = echo(request)

  private def echo(request: Request): Response = {
    println("echo")
    val response = new Response(ProtocolVersion, Status.OK)
    response.setBody(request.getBody)
    response.addHeaderField("Content-Length", response.getBody.length.toString)
    response
  }
}
