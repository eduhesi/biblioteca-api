package br.com.manogarrafa.routes

import br.com.manogarrafa.repositories.impl.GenreRepositoryImpl
import br.com.manogarrafa.usecase.CommonUseCase
import io.ktor.server.routing.*

fun Route.genreRoutes() {
    val repository = GenreRepositoryImpl()
    val useCase = CommonUseCase(repository)
    getBaseRoutes("genre", "genres", useCase)
}