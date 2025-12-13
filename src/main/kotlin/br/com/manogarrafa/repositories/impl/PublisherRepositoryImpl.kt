package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.addNodes
import br.com.manogarrafa.database.deleteNode
import br.com.manogarrafa.database.getCollectionsBy
import br.com.manogarrafa.database.getNodes
import br.com.manogarrafa.database.putNode
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.EditionRequest
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class PublisherRepositoryImpl : CommonRepository<EditionRequest> {
    override val nodeName: String
        get() = "Publisher"

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
        return getCollectionsBy("MATCH (c:Collection)<-[e:EDITION]-(t:Publisher)", name, true)
    }

    override suspend fun addRelationshipWithCollection(
        tags: List<String>,
        collections: List<EditionRequest>
    ): QueryResult<Pair<Int, Int>> {
        val collectionsParam = collections.map { col ->
            mapOf(
                "name" to col.name,
                "cover" to col.cover,
                "number" to col.number,
                "price" to col.price,
                "status" to col.status,
                "quantity" to col.quantity
            )
        }
        val query = $$"""
        WITH $collections AS collections, $tags AS tags
        UNWIND collections AS col
        UNWIND tags AS pub
        MATCH (c:Collection {name: col.name})
        MERGE (p:Publisher {name: pub})
        MERGE (p)-[e:EDITION {number: col.number}]->(c)
        ON CREATE SET
            e.cover = col.cover,
            e.price = col.price * col.quantity,
            e.status = col.status,
            e.quantity = col.quantity
        ON MATCH SET
            e.quantity = coalesce(e.quantity, 0) + col.quantity,
            e.price = coalesce(e.price, 0) + (col.price * col.quantity)
        """.trimIndent()
        val params = mapOf(
            "collections" to collectionsParam,
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