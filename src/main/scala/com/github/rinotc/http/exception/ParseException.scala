package com.github.rinotc.http.exception

class ParseException(private val requestLine: String) extends Exception(s"cannot parser request line: $requestLine") {

  private final val serialVersionUID = 1L
}
