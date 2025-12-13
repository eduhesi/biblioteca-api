package br.com.manogarrafa.usecase

import br.com.manogarrafa.repositories.EditionRepository

class EditionUseCase(private val repository: EditionRepository) {
    suspend fun getFromCollection(collection: String) = repository.getAllEditionsFromCollection(collection)
    suspend fun getFromCollectionByPublisher(collection: String, publisher: String) =
        repository.getAllEditionsFromCollectionByPublisher(collection, publisher)
}