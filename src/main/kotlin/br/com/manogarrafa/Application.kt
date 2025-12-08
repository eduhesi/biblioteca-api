package br.com.manogarrafa

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import br.com.manogarrafa.utils.readFile
import br.com.manogarrafa.utils.showTotal

fun main(args: Array<String>) {
    EngineMain.main(args)
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
