package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.PublisherRepositoryImpl
import io.ktor.server.routing.*

fun Route.publisherRoutes() {
    getBaseRoutes(
        defaultRoute = "publisher",
        route = "publishers",
    ) { PublisherRepositoryImpl() }
}
