package com.github.rinotc.http.webapp

import com.github.rinotc.http.webapp.controller.BasicHttpController

import java.io.IOException
import java.nio.file.{Files, Paths}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}

object Router {
  final val CONTROLLER_PACKAGE_NAME = "com.github.rinotc.http.webapp.controller"

  val routingTable: Map[String, Controller] = {
    val classLoader = Thread.currentThread().getContextClassLoader
    assert(classLoader != null, "classLoader must not be null")
    val resource = classLoader.getResource(CONTROLLER_PACKAGE_NAME.replace('.', '/'))
    val tbl = Try(Paths.get(resource.toURI)) match
      case Failure(_) => Map.empty
      case Success(packageDir) =>
        val table = mutable.HashMap.empty[String, Controller]
        try {
          val filePaths = Files.newDirectoryStream(packageDir, "*.class").asScala
          for (filePath <- filePaths) {
            val simpleName = filePath.getFileName.toString.replaceAll("\\.class$", "")
            val className  = s"$CONTROLLER_PACKAGE_NAME.$simpleName"
            try {
              val instance: Any = Class.forName(className).getDeclaredConstructor().newInstance()
              instance match {
                case controller: Controller => table.put(controller.path, controller)
                case _                      => ()
              }
            } catch {
              case e: InstantiationError     => e.printStackTrace()
              case e: IllegalAccessException => e.printStackTrace()
              case e: ClassNotFoundException => e.printStackTrace()
            }
          }
        } catch {
          case e: IOException =>
            println("fail loading routed classes.")
            e.printStackTrace()
        }
        table.toMap

    println(tbl)
    tbl
  }

  def route(path: String): Controller = {
    val normalized = Paths.get(path).normalize().toString
    println(s"route, normalized: $normalized")
    val rt =
      routingTable.find { case (path, _) => normalized.startsWith(path) }.map(_._2).getOrElse(new BasicHttpController)
    println(rt)
    rt
  }
}
