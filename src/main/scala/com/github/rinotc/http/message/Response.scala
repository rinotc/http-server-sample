package com.github.rinotc.http.message

class Response(
    version: String,
    status: Status
) extends AbstractHttpMessage {
  override protected def startLine: String =
    s"$version ${status.code} ${status.reasonPhrase}"
}
