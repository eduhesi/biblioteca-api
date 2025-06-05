package br.com.manogarrafa

import io.ktor.server.application.*
import utils.readFile

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureFrameworks()
    configureHTTP()
    configureSerialization()
    configureRouting()
    readFile("colecao_completa.txt")
    println("ESTRANGEIRA")
    readFile("colecao_estrangeira_completa.txt")
}
