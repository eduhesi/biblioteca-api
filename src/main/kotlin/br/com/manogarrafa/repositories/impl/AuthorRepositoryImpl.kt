package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.deleteNode
import br.com.manogarrafa.database.getCollectionsBy
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
        return deleteNode(data, "Author")
    }

    override suspend fun getCollection(name: String): QueryResult<List<CollectionResponse>> {
        return getCollectionsBy("MATCH (t:Author)-[:WRITE]->(c:Collection)<-[e:EDITION]-()", name, true)
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