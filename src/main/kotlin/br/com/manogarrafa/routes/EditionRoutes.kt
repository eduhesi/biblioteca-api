package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.EditionRepositoryImpl
import br.com.manogarrafa.usecase.EditionUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail

private val useCase: EditionUseCase by lazy { EditionUseCase(EditionRepositoryImpl()) }

fun Route.editionRoutes() {
    route("editions") {
        getAllEditions()
    }
}

private fun Route.getAllEditions() {
    get("collection") {
        runCatching {
            val collectionName = call.queryParameters.getOrFail("collection")
            response(useCase.getFromCollection(collectionName)) {
                call.respond(HttpStatusCode.OK, it)
            }
        }.recoverCatching {
            when (it) {
                is MissingRequestParameterException -> call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    "Missing query parms"
                )

                else -> call.respond(HttpStatusCode.BadRequest, it.localizedMessage.orEmpty())
            }
        }
    }

    get("collection/{publisherName}") {
        runCatching {
            val collectionName = call.queryParameters.getOrFail("collection")
            val publisherName = call.pathParameters.getOrFail("publisherName")
            response(useCase.getFromCollectionByPublisher(collectionName,publisherName)) {
                call.respond(HttpStatusCode.OK, it)
            }
        }.recoverCatching {
            when (it) {
                is MissingRequestParameterException -> call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    "Missing query parms"
                )

                else -> call.respond(HttpStatusCode.BadRequest, it.localizedMessage.orEmpty())
            }
        }
    }
}