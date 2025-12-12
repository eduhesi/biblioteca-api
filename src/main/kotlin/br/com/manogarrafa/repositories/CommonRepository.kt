package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.PutDefaultEntityRequest

interface CommonRepository<R> {
    suspend fun getAll(): QueryResult<List<String>>
    suspend fun addItems(items: List<String>): QueryResult<Int>
    suspend fun putItem(data: PutDefaultEntityRequest): QueryResult<Boolean>
    suspend fun removeItem(data: String): QueryResult<Boolean>
    suspend fun getCollection(name: String): QueryResult<List<CollectionResponse>>
    suspend fun addRelationshipWithCollection(tags: List<String>, collections: List<R>): QueryResult<Pair<Int, Int>>
}