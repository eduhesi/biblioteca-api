package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.entities.PutDefaultEntityRequest

interface CommonRepository {
    suspend fun getAll(): QueryResult<List<String>>
    suspend fun addItems(items: List<String>): QueryResult<Int>
    suspend fun putItem(data: PutDefaultEntityRequest): QueryResult<Boolean>
    suspend fun removeItem(data: String): QueryResult<Boolean>
}