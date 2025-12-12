package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.GenreRepositoryImpl
import io.ktor.server.routing.Route

fun Route.genreRoutes() {
    getBaseRoutes(
        defaultRoute = "genre",
        routeName = "genres",
    ) { GenreRepositoryImpl() }
}