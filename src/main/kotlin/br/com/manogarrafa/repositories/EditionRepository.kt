package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.entities.CollectionEditionsResponse

interface EditionRepository {
    suspend fun getAllEditionsFromCollection(collection: String): QueryResult<CollectionEditionsResponse>
    suspend fun getAllEditionsFromCollectionByPublisher(
        collection: String,
        publisher: String
    ): QueryResult<CollectionEditionsResponse>
}