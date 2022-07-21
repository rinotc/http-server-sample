package com.github.rinotc.http.server

import com.github.rinotc.http.message.Status

import java.net.ServerSocket
import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.Executors
import scala.util.Try

object SimpleHttpServer:
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

  def main(args: Array[String]): Unit =
    val server = new ServerSocket(Port)
    println("SERVER START: ")
    println(s"LISTENING ON: ${server.getLocalSocketAddress}")

    val executor = Executors.newCachedThreadPool()

    while (true) {
      val socket = server.accept()
      executor.submit(new WorkerThread(socket))
    }
