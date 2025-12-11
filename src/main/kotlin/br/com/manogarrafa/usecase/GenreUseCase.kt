package br.com.manogarrafa.usecase

import br.com.manogarrafa.repositories.GenreRepository

class GenreUseCase(private val repository: GenreRepository) {
    suspend fun getAll() = repository.getAll()
    suspend fun addGenres(items: List<String>) = repository.addGenres(items)
}