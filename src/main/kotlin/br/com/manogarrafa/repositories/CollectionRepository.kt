package br.com.manogarrafa.repositories

import br.com.manogarrafa.entities.AddCollectionRequest

interface CollectionRepository {
    suspend fun getAll(): List<String>
    suspend fun addCollection(request: AddCollectionRequest): Map<String, String>
}