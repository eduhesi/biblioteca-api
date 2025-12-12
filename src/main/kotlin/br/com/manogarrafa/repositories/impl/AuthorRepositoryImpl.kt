package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class AuthorRepositoryImpl : CommonRepository<String> {
    override suspend fun getAll(): QueryResult<List<String>> {
        val query = "MATCH (a: Author) return a.name"
        val result = runQuery {
            it.executeRead { tx ->
                val r = tx.run(query)
                r.list { record -> record.get("a.name").asString() }
            }
        }

        return result
    }

    override suspend fun addItems(items: List<String>): QueryResult<Int> {
        val query = $$"""
        UNWIND $authors AS authorName
            MERGE (a:Author {name: authorName})
        """.trimIndent()

        val result = runQuery {
            val summary = it.executeWrite { tx ->
                tx.run(query, mapOf("authors" to items)).consume()
            }
            summary.counters().nodesCreated()
        }

        return result
    }

    override suspend fun putItem(data: PutDefaultEntityRequest): QueryResult<Boolean> {
        val query = $$"""
        MATCH (a:Author {name: $oldName})
        SET a.name = $newName
        RETURN count(a) as updatedCount
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
        MATCH (a:Author {name: $name})
        DETACH DELETE a
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
        MATCH (a:Author)-[:WRITE]->(c:Collection)<-[e:EDITION]-()
        WHERE a.name = $authorName
        WITH c, collect(e) AS editions
        RETURN
            c.name AS collectionName,
            editions[0].cover AS firstEditionCover,
            c.publicationYear AS year,
            size(editions) AS totalEditions
        ORDER BY collectionName
        """.trimIndent()

        val params = mapOf("authorName" to name)

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
        UNWIND collections AS collectionName
        MATCH (c:Collection {name: collectionName})
        WITH c, tags
        UNWIND tags AS tagName
        MERGE (a:Author {name: tagName})
        MERGE (c)<-[:WRITE]->(a)
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