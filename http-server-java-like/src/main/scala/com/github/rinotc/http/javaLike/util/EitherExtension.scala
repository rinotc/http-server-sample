package com.github.rinotc.http.javaLike.util

object EitherExtension:

  extension [O](either: Either[O, O])
    def unwrap = either match
      case Left(value)  => value
      case Right(value) => value

end EitherExtension
