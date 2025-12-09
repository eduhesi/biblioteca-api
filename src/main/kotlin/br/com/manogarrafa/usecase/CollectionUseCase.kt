package br.com.manogarrafa.usecase

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.repositories.CollectionRepository

class CollectionUseCase(private val repository: CollectionRepository) {
    suspend fun getAll(): List<String> = repository.getAll()
    suspend fun addCollection(request: AddCollectionRequest) = repository.addCollection(request)
}