package br.com.manogarrafa.routes

import br.com.manogarrafa.database.QueryResult
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

        when (val result = CollectionUseCase(CollectionRepositoryImpl()).addCollection(request)) {
            is QueryResult.Success<Any> -> call.response.status(HttpStatusCode.Created)
            is QueryResult.Error -> call.respond(HttpStatusCode.BadRequest, result.message)
        }
    }
}

fun Route.getAllCollectionsRoute() {
    get("/collection") {
        when (val result = CollectionUseCase(CollectionRepositoryImpl()).getAll()) {
            is QueryResult.Success -> call.respond(HttpStatusCode.OK, GetAllCollectionResponse((result).result))
            is QueryResult.Error -> call.respond(HttpStatusCode.BadRequest, result.message)
        }
    }
}