package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.AuthorRepositoryImpl
import br.com.manogarrafa.usecase.CommonUseCase
import io.ktor.server.routing.Route

fun Route.authorRoutes() {
    with(
        BaseRoutes(
            defaultRoute = "author",
            routeName = "authors",
            useCase = CommonUseCase(AuthorRepositoryImpl())
        )
    ) {
        getBaseRoutes()
    }
}
