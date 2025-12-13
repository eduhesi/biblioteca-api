package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.CollectionData
import br.com.manogarrafa.entities.RequestList
import br.com.manogarrafa.entities.ResponseList
import br.com.manogarrafa.repositories.impl.CollectionRepositoryImpl
import br.com.manogarrafa.usecase.CollectionUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.getOrFail

private val useCase: CollectionUseCase by lazy { CollectionUseCase(CollectionRepositoryImpl()) }

fun Route.collectionRoutes() {
    route("/collections") {
        getAll()
        insertCollections()
    }
    route("/collection") {
        addCollection()
        editCollection()
        deleteCollection()
    }
}

private fun Route.addCollection() {
    post {
        val request = call.receive<AddCollectionRequest>()

        response(useCase.addCollection(request)) {
            call.response.status(HttpStatusCode.Created)
        }
    }
}

private fun Route.insertCollections() {
    post {
        val request = call.receive<RequestList<CollectionData>>()
        response(useCase.addCollections(request.items)) {
            //LOg
            println("$it of ${request.items.size} nodes created")
            call.response.status(HttpStatusCode.Created)
        }
    }
}

private fun Route.getAll() {
    get {
        response(useCase.getAll()) {
            call.respond(HttpStatusCode.OK, ResponseList((it)))
        }
    }
}

private fun Route.editCollection() {
    put("/{ref}") {
        val ref = call.pathParameters.getOrFail("ref")
        val data = call.receive<CollectionData>()

        response(useCase.putCollection(ref, data)) { updated ->
            if (updated) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Item '${ref}' não encontrado"))
            }
        }
    }
}

private fun Route.deleteCollection() {
    delete("/{ref}") {
        val ref = call.pathParameters.getOrFail("ref")
        response(useCase.deleteCollection(ref)) { deleted ->
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotModified)
            }
        }
    }
}