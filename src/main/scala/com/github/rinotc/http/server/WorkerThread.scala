package com.github.rinotc.http.server

import java.net.Socket
import java.text.SimpleDateFormat

class WorkerThread(
    private val socket: Socket,
    private val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
) extends Thread {

  override def run(): Unit = {
    val in  = socket.getInputStream
    val out = socket.getOutputStream

  }
}
