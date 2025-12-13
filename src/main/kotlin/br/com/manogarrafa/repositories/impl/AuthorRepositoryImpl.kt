package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.addNodes
import br.com.manogarrafa.database.deleteNode
import br.com.manogarrafa.database.getCollectionsBy
import br.com.manogarrafa.database.getNodes
import br.com.manogarrafa.database.putNode
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class AuthorRepositoryImpl : CommonRepository<String> {
    override val nodeName: String
        get() = "Author"

    override suspend fun getAll(): QueryResult<List<String>> {
        return getNodes()
    }

    override suspend fun addItems(items: List<String>): QueryResult<Int> {
        return addNodes(items)
    }

    override suspend fun putItem(data: PutDefaultEntityRequest): QueryResult<Boolean> {
       return putNode(data)
    }

    override suspend fun removeItem(data: String): QueryResult<Boolean> {
        return deleteNode(data)
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