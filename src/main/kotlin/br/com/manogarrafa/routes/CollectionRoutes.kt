package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.ResponseList
import br.com.manogarrafa.repositories.impl.CollectionRepositoryImpl
import br.com.manogarrafa.usecase.CollectionUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.collectionRoutes() {
    addCollection()
    getAll()
}

private fun Route.addCollection() {
    post("/collection") {
        val request = call.receive<AddCollectionRequest>()

        response(CollectionUseCase(CollectionRepositoryImpl()).addCollection(request)) {
            call.response.status(HttpStatusCode.Created)
        }
    }
}

fun Route.getAll() {
    get("/collections") {
        response(CollectionUseCase(CollectionRepositoryImpl()).getAll()) {
            call.respond(HttpStatusCode.OK, ResponseList((it)))
        }
    }
}