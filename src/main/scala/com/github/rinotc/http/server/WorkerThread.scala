package com.github.rinotc.http.server

import com.github.rinotc.http.message.{Request, Response}
import com.github.rinotc.http.webapp.{Controller, Router}

import java.io.{IOException, InputStream, OutputStream}
import java.net.Socket
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import scala.util.{Failure, Success, Try}

class WorkerThread(
    private val socket: Socket
) extends Thread {

  println("new WorkerThread")

  override def run(): Unit = {
    try {
      val in: InputStream   = socket.getInputStream
      val out: OutputStream = socket.getOutputStream
      val response          = handleRequest(in)

      val message = response.serializeResponse()
      out.write(message)
    } catch {
      case e: IOException => errorLog("something happened", e)
    }
  }

  private def handleRequest(in: InputStream): Response = {
    val request: Request       = Request.parseRequest(in)
    val controller: Controller = Router.route(request.target)
    val response: Response     = controller.handle(request)
    accessLog(request.startLine, response.statusCode)
    response
  }

  /**
   * アクセスログを出力します
   *
   * @param requestLine
   *   リクエスト
   * @param responseCode
   *   レスポンスコード
   */
  private def accessLog(requestLine: String, responseCode: Int): Unit = {
    val date = LocalDateTime.now()
    println(s"$date $requestLine $responseCode")
  }

  /**
   * エラーログを出力します
   *
   * @param message
   *   エラーメッセージ
   */
  private def errorLog(message: String, e: Throwable): Unit = {
    val date = LocalDateTime.now()
    println(s"[$date] [ERROR] $message")
    e.printStackTrace()
  }
}
