package br.com.manogarrafa.routes

import br.com.manogarrafa.entities.RequestList
import br.com.manogarrafa.entities.ResponseList
import br.com.manogarrafa.repositories.impl.GenreRepositoryImpl
import br.com.manogarrafa.usecase.GenreUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.genreRoutes() {
    getAll()
    addGenre()
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