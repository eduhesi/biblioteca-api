package br.com.manogarrafa.usecase.collection

import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.repositories.collection.PostCollectionRepository

class PostCollection(private val repository: PostCollectionRepository) {
    suspend operator fun invoke(request: AddCollectionRequest): Map<String, String> {
        return repository(request)
    }
}