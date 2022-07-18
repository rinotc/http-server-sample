package com.github.rinotc.http.message

import com.github.rinotc.http.exception.ParseException
import com.github.rinotc.http.message.AbstractHttpMessage

import java.io.{BufferedReader, ByteArrayOutputStream, InputStream, InputStreamReader}
import scala.util.matching.Regex

class Request(
    val method: Method,
    val target: String,
    val version: String
) extends AbstractHttpMessage {
  override def startLine: String = {
    s"$method $target $version"
  }
}

object Request {

  private val RequestLinePattern: Regex = "^(?<method>\\S+) (?<target>\\S+) (?<version>\\S+)".r

  private val HeaderPattern: Regex = "^(?<name>\\S+):[ \\t]?(?<value>.+)[ \\t]?$".r

  private val EMPTY = ""

  def parseRequest(in: InputStream): Request = {
    val br      = new BufferedReader(new InputStreamReader(in))
    val request = parserRequestLine(br)
    parseHeaderLines(br, request)
    parseBody(br, request)

    request
  }

  /**
   * @throws IOException
   * @throws ParseException
   */
  private def parserRequestLine(br: BufferedReader): Request = {
    val requestLine = br.readLine()

    requestLine match {
      case RequestLinePattern(method, target, version) =>
        new Request(Method.valueOf(method), target, version)
      case _ =>
        throw new ParseException(requestLine)
    }
  }

  /**
   * @param br
   *   [[BufferedReader]]
   * @param request
   *   リクエスト
   * @throws IOException
   *   [[BufferedReader]] が投げた場合
   * @throws ParseException
   *   パースに失敗した時
   */
  private def parseHeaderLines(br: BufferedReader, request: Request): Unit = {
    var flag = true
    while (flag) {
      val headerField = br.readLine()
      if headerField.trim == EMPTY then flag = false
      else {
        headerField match
          case HeaderPattern(name, value) =>
            request.addHeaderField(name.toLowerCase(), value)
          case _ =>
            throw new ParseException(headerField)
      }
    }
  }

  private def parseBody(br: BufferedReader, request: Request): Unit = {
    if request.headers.contains("transfer-encoding") then parseChunkedBody(br, request)
    else if request.headers.contains("content-length") then parseSimpleBody(br, request)
    else () // nothing to read
  }

  private def parseChunkedBody(br: BufferedReader, request: Request): Unit = {
    val transferEncoding = request.headers("transfer-encoding")
    if (transferEncoding == "chunked") {
      var length       = 0
      val body         = new ByteArrayOutputStream()
      var chunkSizeHex = br.readLine().replaceFirst(" .*$", "")
      var chunkSize    = Integer.parseInt(chunkSizeHex, 16)
      while (chunkSize > 0) {
        val chunk = Array.emptyCharArray
        br.read(chunk, 0, chunkSize)
        br.skip(2) // CRLF
        body.write(String.valueOf(chunk).getBytes())
        length += chunk.length

        chunkSizeHex = br.readLine().replaceFirst(" .*$", "")
        chunkSize = Integer.parseInt(chunkSizeHex, 16)
      }

      request.addHeaderField("content-length", length.toString)
      request.headers.remove("transfer-encoding")
      request.setBody(body.toByteArray)
    }
  }

  private def parseSimpleBody(br: BufferedReader, request: Request): Unit = {
    val contentLength = request.headers("content-length").toInt
    val body          = Array.emptyCharArray
    br.read(body, 0, contentLength)
    request.setBody(String.valueOf(body).getBytes())
  }
}
