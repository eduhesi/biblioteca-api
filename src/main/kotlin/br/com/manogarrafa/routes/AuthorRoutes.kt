package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.AuthorRepositoryImpl
import io.ktor.server.routing.Route

fun Route.authorRoutes() {
    getBaseRoutes(
        defaultRoute = "author",
        route = "authors",
    ) { AuthorRepositoryImpl() }
}
