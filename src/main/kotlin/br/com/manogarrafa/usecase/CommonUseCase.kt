package br.com.manogarrafa.usecase

import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class CommonUseCase(private val repository: CommonRepository) {
    suspend fun getAll() = repository.getAll()
    suspend fun addItems(items: List<String>) = repository.addItems(items)
    suspend fun putItem(data: PutDefaultEntityRequest) = repository.putItem(data)
    suspend fun removeItem(name: String) = repository.removeItem(name)
    suspend fun getCollection(name: String) = repository.getCollection(name)
}