package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.PublisherRepositoryImpl
import br.com.manogarrafa.usecase.CommonUseCase
import io.ktor.server.routing.*

fun Route.publisherRoutes() {
    val repository = PublisherRepositoryImpl()
    val useCase = CommonUseCase(repository)
    getBaseRoutes("publisher", "publishers", useCase)
}
