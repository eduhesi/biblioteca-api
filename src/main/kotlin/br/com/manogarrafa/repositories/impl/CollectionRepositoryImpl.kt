package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.getCollectionsBy
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.entities.CollectionData
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

    override suspend fun addItems(items: List<CollectionData>): QueryResult<Int> {
        val itemsMap = items.map { item ->
            mapOf(
                "name" to item.name,
                "publicationYear" to item.publicationYear,
                "complete" to item.complete,
            )
        }
        val query = $$"""
            UNWIND $items AS item
            MERGE (c:Collection {name: item.name})
            ON CREATE SET c.publicationYear = item.publicationYear,
                          c.complete = item.complete
        """.trimIndent()

        val result = runQuery {
            val summary = it.executeWrite { tx ->
                tx.run(query, mapOf("items" to itemsMap)).consume()
            }
            summary.counters().nodesCreated()
        }

        return result
    }

    override suspend fun putItem(ref: String, data: CollectionData): QueryResult<Boolean> {
        val query = $$"""
        MERGE (c:Collection {name: $ref})
        SET c.publicationYear = $year,
            c.complete = $complete,
            c.name = $name
        RETURN count(c) as updatedCount
        """.trimIndent()
        val params = mapOf(
            "ref" to ref,
            "year" to data.publicationYear,
            "complete" to data.complete,
            "name" to data.name
        )
        return runQuery { session ->
            session.executeWrite { tx ->
                val result = tx.run(query, params)
                val updatedCount = result.single()["updatedCount"].asInt()
                updatedCount > 0 // true se houve alteração, false caso contrário
            }
        }
    }

    override suspend fun removeItem(data: String): QueryResult<Boolean> {
        val query = $$"""
        MATCH (p: Collection {name: $name})
        DETACH DELETE p
        """.trimIndent()
        val params = mapOf("name" to data)
        return runQuery { session ->
            session.executeWrite { tx ->
                val result = tx.run(query, params)
                val nodesDeleted = result.consume().counters().nodesDeleted()
                nodesDeleted > 0 // true se removeu, false caso contrário
            }
        }
    }
}