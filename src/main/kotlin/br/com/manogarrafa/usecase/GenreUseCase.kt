package br.com.manogarrafa.usecase

import br.com.manogarrafa.entities.GenreRequest
import br.com.manogarrafa.repositories.GenreRepository

class GenreUseCase(private val repository: GenreRepository) {
    suspend fun getAll() = repository.getAll()
    suspend fun addGenres(items: List<String>) = repository.addGenres(items)
    suspend fun putGenre(data: GenreRequest) = repository.putGenre(data)
    suspend fun removeGenre(name: String) = repository.removeGenre(name)
}