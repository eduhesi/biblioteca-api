package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.entities.RequestList
import br.com.manogarrafa.entities.ResponseList
import br.com.manogarrafa.repositories.CommonRepository
import br.com.manogarrafa.usecase.CommonUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getBaseRoutes(
    /** edit, delete */
    defaultRoute: String,
    /** getAll, insert */
    route: String,
    repository: () -> CommonRepository
) {
    val useCase: CommonUseCase by lazy { CommonUseCase(repository()) }

    getAll(route, useCase)
    insertItem(route, useCase)
    editItem(defaultRoute, useCase)
    deleteItem(defaultRoute, useCase)
}

private fun Route.getAll(route: String, useCase: CommonUseCase) {
    get("/$route") {
        response(useCase.getAll()) {
            call.respond(HttpStatusCode.OK, ResponseList(it))
        }
    }
}

private fun Route.insertItem(route: String, useCase: CommonUseCase) {
    post("/$route") {
        val request = call.receive<RequestList<String>>()
        response(useCase.addItems(request.items)) {
            //LOg
            println("$it of ${request.items.size} nodes created")
            call.response.status(HttpStatusCode.Created)
        }
    }
}

private fun Route.editItem(route: String, useCase: CommonUseCase) {
    put("/$route") {
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

private fun Route.deleteItem(route: String, useCase: CommonUseCase) {
    delete("/$route/item/{itemName}") {
        val itemName = call.pathParameters["itemName"]

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