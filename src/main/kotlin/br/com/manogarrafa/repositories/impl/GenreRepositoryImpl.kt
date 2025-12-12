package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class GenreRepositoryImpl : CommonRepository<String> {
    override suspend fun getAll(): QueryResult<List<String>> {
        val query = "MATCH (g: Genre) return g.name"
        val result = runQuery {
            it.executeRead { tx ->
                val r = tx.run(query)
                r.list { record -> record.get("g.name").asString() }
            }
        }

        return result
    }

    override suspend fun addItems(items: List<String>): QueryResult<Int> {
        val query = $$"""
        UNWIND $genres AS genreName
            MERGE (g:Genre {name: genreName})
        """.trimIndent()

        val result = runQuery {
            val summary = it.executeWrite { tx ->
                tx.run(query, mapOf("genres" to items)).consume()
            }
            summary.counters().nodesCreated()
        }

        return result
    }

    override suspend fun putItem(data: PutDefaultEntityRequest): QueryResult<Boolean> {
        val query = $$"""
        MATCH (g:Genre {name: $oldName})
        SET g.name = $newName
        RETURN count(g) as updatedCount
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
        MATCH (g:Genre {name: $name})
        DETACH DELETE g
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
        MATCH (g:Genre)<-[:HAS_GENRE]-(c:Collection)<-[e:EDITION]-()
        WHERE g.name = $genreName
        WITH c, collect(e) AS editions
        RETURN
            c.name AS collectionName,
            editions[0].cover AS firstEditionCover,
            c.publicationYear AS year,
            size(editions) AS totalEditions
        ORDER BY collectionName
        """.trimIndent()

        val params = mapOf("genreName" to name)

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
        MERGE (g:Genre {name: tagName})
        MERGE (c)-[:HAS_GENRE]->(g)
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