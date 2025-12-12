package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.EditionRequest
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class PublisherRepositoryImpl : CommonRepository {
    override suspend fun getAll(): QueryResult<List<String>> {
        val query = "MATCH (p: Publisher) return p.name"
        val result = runQuery {
            it.executeRead { tx ->
                val r = tx.run(query)
                r.list { record -> record.get("p.name").asString() }
            }
        }

        return result
    }

    override suspend fun addItems(items: List<String>): QueryResult<Int> {
        val query = $$"""
        UNWIND $publishers AS publisherName
            MERGE (p:Publisher {name: publisherName})
        """.trimIndent()

        val result = runQuery {
            val summary = it.executeWrite { tx ->
                tx.run(query, mapOf("publishers" to items)).consume()
            }
            summary.counters().nodesCreated()
        }

        return result
    }

    override suspend fun putItem(data: PutDefaultEntityRequest): QueryResult<Boolean> {
        val query = $$"""
        MATCH (p:Publisher {name: $oldName})
        SET p.name = $newName
        RETURN count(p) as updatedCount
    """.trimIndent()
        val params = mapOf("oldName" to data.oldName, "newName" to data.newName)
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
        MATCH (p:Publisher {name: $name})
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

    override suspend fun getCollection(name: String): QueryResult<List<CollectionResponse>> {
        val query = $$"""
        MATCH (c:Collection)<-[e:EDITION]-(p:Publisher)
        WHERE p.name = $publisherName
        WITH c, collect(e) AS editions
        RETURN
            c.name AS collectionName,
            editions[0].cover AS firstEditionCover,
            c.publicationYear AS year,
            size(editions) AS totalEditions
        ORDER BY collectionName
        """.trimIndent()

        val params = mapOf("publisherName" to name)

        val resultList = runQuery {
            it.executeRead { tx ->
                val result = tx.run(query, params)
                result.list { record ->
                    CollectionResponse(
                        name = record.get("collectionName").asString(),
                        cover = record.get("firstEditionCover").asString(),
                        publicationYear = record.get("year").asInt(),
                        totalEditions = record.get("totalEditions").asInt()
                    )
                }
            }
        }
        return resultList
    }

    override suspend fun addRelationshipWithCollection(
        tags: List<String>,
        collections: List<String>
    ): QueryResult<Pair<Int, Int>> {
        val query = $$"""
        WITH $collections AS collections, $tags AS tags
        UNWIND collections AS col, UNWIND tags AS pub
        MATCH (c:Collection {name: col.name})
        MERGE (p:Publisher {name: tagName})
        WITH c, p
        MERGE (p)-[:EDITION {number: col.number}]->(c)
        ON CREATE SET
            e.cover = col.cover,
            e.price = col.price,
            e.status = col.status,
            e.quantity = col.quantity
        ON MATCH SET
            e.quantity = coalesce(e.quantity, 0) + col.quantity,
            e.price = coalesce(e.price, 0) + col.price,
        """.trimIndent()
        val params = mapOf(
            "collections" to collections,
            "tags" to tags
        )

        val result = runQuery {
            val summary = it.executeWrite { tx ->
                tx.run(query, params).consume()
            }
            val nodesCreated = summary.counters().nodesCreated()
            val relationshipsCreated = summary.counters().relationshipsCreated()
            nodesCreated to relationshipsCreated
        }

        return result
    }
}