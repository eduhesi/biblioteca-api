package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.AuthorRepositoryImpl
import br.com.manogarrafa.usecase.CommonUseCase
import io.ktor.server.routing.Route

fun Route.authorRoutes() {
    val repository = AuthorRepositoryImpl()
    val useCase = CommonUseCase(repository)
    getBaseRoutes("author", "authors", useCase)
}
