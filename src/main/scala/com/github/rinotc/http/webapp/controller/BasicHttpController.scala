package com.github.rinotc.http.webapp.controller

import com.github.rinotc.http.message.{Request, Response}
import com.github.rinotc.http.webapp.Controller

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
  override def doPost(request: Request): Response = ???

  /**
   * GET リクエストを受け取った場合に実行されるメソッド
   *
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  override def doGet(request: Request): Response = ???
}

object BasicHttpController {

  val protocolVersion = "HTTP/1.1"
}
