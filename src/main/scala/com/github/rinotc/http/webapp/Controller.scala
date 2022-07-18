package com.github.rinotc.http.webapp

import com.github.rinotc.http.message.Method.{CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE}
import com.github.rinotc.http.message.{Request, Response}

abstract class Controller {

  val path: String

  /**
   * POST リクエストを受け取った場合に実行されるメソッド
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  def doPost(request: Request): Response

  /**
   * GET リクエストを受け取った場合に実行されるメソッド
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   */
  def doGet(request: Request): Response

  /**
   * request の HTTP メソッドによってメソッドの売り分けを行い、レスポンスを返す
   * @param request
   *   HTTP Request
   * @return
   *   HTTP Response
   * @throws UnsupportedOperationException
   *   GET, POST 以外のメソッドのリクエストが渡された場合
   */
  def handle(request: Request): Response =
    request.method match
      case GET  => doGet(request)
      case POST => doPost(request)
      case _    => throw new UnsupportedOperationException(request.method.toString)
}
