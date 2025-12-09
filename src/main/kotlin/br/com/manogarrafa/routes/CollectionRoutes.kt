package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.repositories.impl.CollectionRepositoryImpl
import br.com.manogarrafa.usecase.CollectionUseCase
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addCollectionRoute() {
    post("/collection") {
        val request = call.receive<AddCollectionRequest>()

        val result = CollectionUseCase(CollectionRepositoryImpl()).addCollection(request)

        call.respond(result)
    }
}

fun Route.getAllCollectionsRoute() {
    get("/collection") {
        val result = CollectionUseCase(CollectionRepositoryImpl()).getAll()
        call.respond(result)
    }
}