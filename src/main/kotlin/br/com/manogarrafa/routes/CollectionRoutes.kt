package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.GetAllCollectionResponse
import br.com.manogarrafa.repositories.impl.CollectionRepositoryImpl
import br.com.manogarrafa.usecase.CollectionUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addCollectionRoute() {
    post("/collection") {
        val request = call.receive<AddCollectionRequest>()

        response(CollectionUseCase(CollectionRepositoryImpl()).addCollection(request)) {
            call.response.status(HttpStatusCode.Created)
        }
    }
}

fun Route.getAllCollectionsRoute() {
    get("/collection") {
        response(CollectionUseCase(CollectionRepositoryImpl()).getAll()) {
            call.respond(HttpStatusCode.OK, GetAllCollectionResponse((it)))
        }
    }
}