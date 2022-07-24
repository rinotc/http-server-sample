package com.github.rinotc.http.message

import java.io.ByteArrayOutputStream

class Response(
    val version: String,
    private val status: Status
) extends AbstractHttpMessage {

//  private val CRLF = "\r\n"
  private val LF = "\n"

  val statusCode: Int = status.code

  val reasonPhrase: String = status.reasonPhrase

  override def startLine: String =
    s"$version ${status.code} ${status.reasonPhrase}"

  def serializeResponse(): Array[Byte] = {
    val message: ByteArrayOutputStream = new ByteArrayOutputStream

    message.write(s"$version $statusCode $reasonPhrase $LF".getBytes())
    headers.foreachEntry { case (key, value) =>
      message.write(s"$key: $value $LF".getBytes())
    }
    message.write(LF.getBytes())

    // ボディはファイルから読み取ったバイト列をそのまま書き込む
    message.write(body)
    message.toByteArray
  }
}
