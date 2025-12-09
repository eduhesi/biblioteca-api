package br.com.manogarrafa.repositories.collection

import br.com.manogarrafa.entities.AddCollectionRequest

fun interface PostCollectionRepository {
    suspend operator fun invoke(request: AddCollectionRequest): Map<String, String>
}