package com.github.rinotc.http.message

import com.github.rinotc.http.message.AbstractHttpMessage

class Request(
    val method: Method,
    val target: String,
    val version: String
) extends AbstractHttpMessage {
  override protected def startLine: String = {
    s"$method $target $version"
  }
}
