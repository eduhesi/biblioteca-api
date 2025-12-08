package br.com.manogarrafa.routes.collection

import br.com.manogarrafa.entities.AddCollectionRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addCollectionRoute() {
    post("/collection") {
        val request = call.receive<AddCollectionRequest>()

        // useCase()

        call.respond(mapOf("status" to "success"))


    }
}