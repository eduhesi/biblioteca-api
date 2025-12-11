package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.GenreRequest
import br.com.manogarrafa.entities.RequestList
import br.com.manogarrafa.entities.ResponseList
import br.com.manogarrafa.repositories.impl.GenreRepositoryImpl
import br.com.manogarrafa.usecase.GenreUseCase
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.genreRoutes() {
    getAll()
    addGenre()
    editGenre()
    removeGenre()
}

private fun Route.getAll() {
    get("/genres") {
        response(GenreUseCase(GenreRepositoryImpl()).getAll()) {
            call.respond(HttpStatusCode.OK, ResponseList(it))
        }
    }
}

private fun Route.addGenre() {
    post("/genres") {
        val request = call.receive<RequestList<String>>()
        response(GenreUseCase(GenreRepositoryImpl()).addGenres(request.items)) {
            //LOg
            println("$it of ${request.items.size} nodes created")
            call.response.status(HttpStatusCode.Created)
        }
    }
}

private fun Route.editGenre() {
    put("/genre") {
        val request = call.receive<GenreRequest>()

        // Validação simples
        if (request.oldName.isBlank() || request.newName.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nome antigo e novo não podem ser vazios"))
            return@put
        }

        response(GenreUseCase(GenreRepositoryImpl()).putGenre(request)) { updated ->
            if (updated) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Gênero '${request.oldName}' não encontrado"))
            }
        }
    }
}

private fun Route.removeGenre() {
    @Serializable
    @Resource("/genre")
    class DeleteRoute(val name: String)

    delete<DeleteRoute> { params ->
        response(GenreUseCase(GenreRepositoryImpl()).removeGenre(params.name)) { deleted ->
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotModified)
            }
        }
    }
}