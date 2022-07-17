package com.github.rinotc.http.message

import java.nio.charset.StandardCharsets
import scala.collection.mutable

abstract class AbstractHttpMessage(
    val headers: mutable.Map[String, String] = new mutable.HashMap[String, String](),
    protected var body: Array[Byte] = Array.emptyByteArray
) {

  def addHeaderField(name: String, value: String): Unit = {
    headers.put(name, value)
  }

  def setBody(body: Array[Byte]): Unit = {
    this.body = body
  }

  def getBody: Array[Byte] = this.body

  protected def startLine: String

  override def toString: String = {
    val bodyString = new String(body, StandardCharsets.UTF_8)
    s"$startLine headers: $headers body: $bodyString"
  }
}
