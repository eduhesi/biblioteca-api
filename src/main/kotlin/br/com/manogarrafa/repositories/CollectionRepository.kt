package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.CollectionData
import br.com.manogarrafa.entities.CollectionResponse

interface CollectionRepository {
    suspend fun getAll(): QueryResult<List<CollectionResponse>>
    suspend fun addCollectionWithAuthorAndPublisherAndGenre(request: AddCollectionRequest): QueryResult<Any>
    suspend fun addItems(items: List<CollectionData>): QueryResult<Int>
    suspend fun putItem(ref: String, data: CollectionData): QueryResult<Boolean>
    suspend fun removeItem(data: String): QueryResult<Boolean>
}