package com.github.rinotc.http.server

import java.net.ServerSocket
import java.nio.file.Paths
import java.util.concurrent.Executors

object SimpleHttpServer:
  val port                         = 8080
  private val documentRoot: String = Paths.get(System.getProperty("user.dir"), "files", "www").toString
  private val mimeTypes = Map(
    "html" -> "text/html",
    "css"  -> "text/css",
    "js"   -> "application/js",
    "png"  -> "image/png",
    "txt"  -> "text/plain"
  )

  def main(args: Array[String]): Unit =
    val server   = new ServerSocket(port)
    val executor = Executors.newCachedThreadPool()

    while (true) {
      val socket = server.accept()

      // socket オブジェクトを渡して各リクエストの処理は別スレッドで
//      executor.submit(new WorkerThread())
    }
