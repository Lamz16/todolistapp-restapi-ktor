package com.lamz.core

import com.lamz.data.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 5000) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureRouting()
}
