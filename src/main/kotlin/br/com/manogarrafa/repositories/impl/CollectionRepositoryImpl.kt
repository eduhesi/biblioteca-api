package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.getCollectionsBy
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.repositories.CollectionRepository

class CollectionRepositoryImpl : CollectionRepository {
    override suspend fun getAll(): QueryResult<List<CollectionResponse>> {
        return getCollectionsBy("MATCH (c:Collection)<-[e:EDITION]-()", "", false)
    }

    override suspend fun addCollectionWithAuthorAndPublisherAndGenre(request: AddCollectionRequest): QueryResult<Any> {
        val query = $$"""
        CREATE (c:Collection {name: $name, publicationYear: $year, complete: $complete})
        WITH c
        UNWIND $authors AS authorName
            MERGE (a:Author {name: authorName})
            MERGE (a)-[:WRITE]->(c)
        WITH c
        MERGE (p:Publisher {name: $publisher})
        WITH c
        UNWIND $genres AS genreName
            MERGE (g:Genre {name: genreName})
            MERGE (c)-[:HAS_GENRE]->(g)
        """.trimIndent()
        return runQuery { session ->
            session.executeWrite { tx ->
                tx.run(
                    query, mapOf(
                        "name" to request.collection.name,
                        "year" to request.collection.publicationYear,
                        "complete" to request.collection.complete,
                        "authors" to request.author,
                        "publisher" to request.publisher,
                        "genres" to request.genre
                    )
                ).consume()
            }
        }
    }
}