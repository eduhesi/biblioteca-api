package br.com.manogarrafa

import io.ktor.server.application.*
import utils.readFile
import utils.showList
import utils.showTotal

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureFrameworks()
    configureHTTP()
    configureSerialization()
    configureRouting()
    readFile("colecao_completa.txt")
    readFile("colecao_estrangeira_completa.txt")
    showTotal()
}
