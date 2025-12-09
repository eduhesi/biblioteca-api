package br.com.manogarrafa.routes.collection

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.repositories.collection.impl.PostCollectionRepositoryImpl
import br.com.manogarrafa.usecase.collection.PostCollection
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addCollectionRoute() {
    post("/collection") {
        val request = call.receive<AddCollectionRequest>()

        val result = PostCollection(PostCollectionRepositoryImpl())(request)

        call.respond(result)
    }
}