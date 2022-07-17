package com.github.rinotc.http.message

enum Status(val code: Int, val reasonPhrase: String):
  case OK extends Status(200, "OK")
  case NO_CONTENT extends Status(204, "No Content")
  case BAD_REQUEST extends Status(400, "Bad Request")
  case NOT_FOUND extends Status(404, "Not Found")
