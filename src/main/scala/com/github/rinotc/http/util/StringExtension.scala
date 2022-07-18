package com.github.rinotc.http.util

object StringExtension:

  private val ExtSplitChar = "."

  extension (fileName: String)
    def fileExtension: String =
      val pos = fileName.lastIndexOf(ExtSplitChar)
      if pos > 0 then fileName.substring(pos + 1)
      else ""
  end extension

end StringExtension
