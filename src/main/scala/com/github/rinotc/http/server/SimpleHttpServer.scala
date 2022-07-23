package com.github.rinotc.http.server

import com.github.rinotc.http.message.{Request, Response, Status}
import com.github.rinotc.http.webapp.{Controller, Router}

import java.io.InputStream
import java.net.ServerSocket
import java.nio.file.{Files, Path, Paths}
import java.time.LocalDateTime
import scala.util.Try
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.global

object SimpleHttpServer {
  val Port = 8080

  val documentRoot: String = Paths.get(System.getProperty("user.dir"), "files", "www").toString

  val mimeTypes: Map[String, String] = Map(
    "html" -> "text/html",
    "css"  -> "text/css",
    "js"   -> "application/js",
    "png"  -> "image/png",
    "txt"  -> "text/plain"
  )

  val errorPages: Map[Status, Path] = Map(
    Status.BAD_REQUEST -> Paths.get(documentRoot, "error/400.html"),
    Status.NOT_FOUND   -> Paths.get(documentRoot, "error/404.html")
  )

  def extensionToContentType(ext: String): String = mimeTypes.getOrElse(ext, "")

  def readErrorPage(status: Status): Array[Byte] = {
    errorPages.get(status) match
      case None => Array.emptyByteArray
      case Some(path) =>
        Try(Files.readAllBytes(path)).getOrElse(Array.emptyByteArray)
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

  private def handleRequest(in: InputStream): Response = {
    val request: Request       = Request.parseRequest(in)
    val controller: Controller = Router.route(request.target)
    val response: Response     = controller.handle(request)
    accessLog(request.startLine, response.statusCode)
    response
  }

  def main(args: Array[String]): Unit =
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    val server                        = new ServerSocket(Port)
    println("SERVER START: ")
    println(s"LISTENING ON: ${server.getLocalSocketAddress}")

    while (true) {
      val socket = server.accept()
      Future {
        val in       = socket.getInputStream
        val out      = socket.getOutputStream
        val response = handleRequest(in)
        out.write(response.serializeResponse())
        out.flush()
      }.onComplete(_ => socket.close())
    }
}
