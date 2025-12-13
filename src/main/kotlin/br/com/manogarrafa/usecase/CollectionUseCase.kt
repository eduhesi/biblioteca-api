package br.com.manogarrafa.usecase

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.CollectionData
import br.com.manogarrafa.repositories.CollectionRepository

class CollectionUseCase(private val repository: CollectionRepository) {
    suspend fun getAll() = repository.getAll()
    suspend fun addCollection(request: AddCollectionRequest) =
        repository.addCollectionWithAuthorAndPublisherAndGenre(request)

    suspend fun putCollection(ref: String, data: CollectionData) = repository.putItem(ref, data)
    suspend fun deleteCollection(ref: String) = repository.removeItem(ref)
    suspend fun addCollections(request: List<CollectionData>)  = repository.addItems(request)
}