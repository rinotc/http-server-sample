package com.github.rinotc.http.message

import java.io.ByteArrayOutputStream

class Response(
    val version: String,
    private val status: Status
) extends AbstractHttpMessage {

  private val CRLF = "\r\n"

  val statusCode: Int = status.code

  val reasonPhrase: String = status.reasonPhrase

  override def startLine: String =
    s"$version ${status.code} ${status.reasonPhrase}"

  def serializeResponse(): Array[Byte] = {
    val message: ByteArrayOutputStream = new ByteArrayOutputStream

    message.write(s"$version $statusCode $reasonPhrase $CRLF".getBytes())
    headers.foreachEntry { case (key, value) =>
      message.write(s"$key: $value $CRLF".getBytes())
    }
    message.write(CRLF.getBytes())

    // ボディはファイルから読み取ったバイト列をそのまま書き込む
    message.write(body)
    message.toByteArray
  }
}
