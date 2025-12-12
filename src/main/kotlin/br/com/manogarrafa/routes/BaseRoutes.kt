package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.CreateRelationshipRequest
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.entities.RequestList
import br.com.manogarrafa.entities.ResponseList
import br.com.manogarrafa.usecase.CommonUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

class BaseRoutes(
    /** edit, delete */
    val defaultRoute: String,
    /** getAll, insert */
    val routeName: String,
    val useCase: CommonUseCase
) {

    fun Route.getBaseRoutes() {
        route(routeName) {
            getAll()
            insertItem()
            addRelationship()
        }
        route(defaultRoute) {
            getItem()
            editItem()
            deleteItem()
        }
    }

    private fun Route.getItem() {
        get("/{name}") {
            val name = call.pathParameters.getOrFail("name").trim()
            response(useCase.getCollection(name)) {
                call.respond(HttpStatusCode.OK, ResponseList(it))
            }
        }
    }

    private fun Route.getAll() {
        get {
            response(useCase.getAll()) {
                call.respond(HttpStatusCode.OK, ResponseList(it))
            }
        }
    }

    private fun Route.insertItem() {
        post {
            val request = call.receive<RequestList<String>>()
            response(useCase.addItems(request.items)) {
                //LOg
                println("$it of ${request.items.size} nodes created")
                call.response.status(HttpStatusCode.Created)
            }
        }
    }

    private fun Route.addRelationship() {
        post("/relationship") {
            val request = call.receive<CreateRelationshipRequest<String>>()
            response(useCase.addRelationshipWithCollection(request.tags, request.collections)) {
                println(
                    "${it.first} of ${request.tags.size} nodes created\n" +
                            "${it.second} of ${request.tags.size * request.collections.size} relationships created"
                )
                call.response.status(HttpStatusCode.Created)
            }
        }
    }

    private fun Route.editItem() {
        put {
            val request = call.receive<PutDefaultEntityRequest>()

            // Validação simples
            if (request.oldName.isBlank() || request.newName.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nome antigo e novo não podem ser vazios"))
                return@put
            }

            response(useCase.putItem(request)) { updated ->
                if (updated) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Item '${request.oldName}' não encontrado"))
                }
            }
        }
    }

    private fun Route.deleteItem() {
        delete("/{name}") {
            val itemName = call.pathParameters["name"]

            if (itemName.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nome não podem ser vazios"))
                return@delete
            }
            response(useCase.removeItem(itemName)) { deleted ->
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotModified)
                }
            }
        }
    }
}