package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.CollectionResponse

interface CollectionRepository {
    suspend fun getAll(): QueryResult<List<CollectionResponse>>
    suspend fun addCollection(request: AddCollectionRequest): QueryResult<Any>
}