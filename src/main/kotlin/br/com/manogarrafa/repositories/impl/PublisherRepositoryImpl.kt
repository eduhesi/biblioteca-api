package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.deleteNode
import br.com.manogarrafa.database.getCollectionsBy
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.EditionRequest
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

class PublisherRepositoryImpl : CommonRepository<EditionRequest> {
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
       return deleteNode(data, "Publisher")
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