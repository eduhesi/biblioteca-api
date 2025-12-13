package br.com.manogarrafa.database

import br.com.manogarrafa.entities.CollectionResponse
import br.com.manogarrafa.entities.PutDefaultEntityRequest
import br.com.manogarrafa.repositories.CommonRepository

fun getCollectionsBy(match: String, param: String, hasFilter: Boolean): QueryResult<List<CollectionResponse>> {
    val filter = if (hasFilter) {
        $$"WHERE t.name = $tagName"
    } else {
        ""
    }

    val query = $$"""
        $$match
        $$filter
        WITH c, collect(e) AS editions
        RETURN
            c.name AS collectionName,
            editions[0].cover AS firstEditionCover,
            c.publicationYear AS year,
            reduce(total = 0, ed IN editions | total + coalesce(ed.quantity, 0)) AS totalEditions
        ORDER BY collectionName
        """.trimIndent()

    val params = mapOf("tagName" to param)

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

fun <R> CommonRepository<R>.deleteNode(name: String): QueryResult<Boolean> {
    val query = $$"""
        MATCH (p: $$nodeName {name: $name})
        DETACH DELETE p
        """.trimIndent()
    val params = mapOf("name" to name)
    return runQuery { session ->
        session.executeWrite { tx ->
            val result = tx.run(query, params)
            val nodesDeleted = result.consume().counters().nodesDeleted()
            nodesDeleted > 0 // true se removeu, false caso contrário
        }
    }
}

fun <R> CommonRepository<R>.putNode(data: PutDefaultEntityRequest): QueryResult<Boolean> {
    val query = $$"""
        MATCH (a:$$nodeName {name: $oldName})
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

fun <R> CommonRepository<R>.addNodes(items: List<String>): QueryResult<Int> {
    val query = $$"""
        UNWIND $items AS itemName
            MERGE (g:$$nodeName {name: itemName})
        """.trimIndent()

    val result = runQuery {
        val summary = it.executeWrite { tx ->
            tx.run(query, mapOf("items" to items)).consume()
        }
        summary.counters().nodesCreated()
    }

    return result
}

fun <R> CommonRepository<R>.getNodes(): QueryResult<List<String>> {
    val query = "MATCH (g: $nodeName) return g.name"
    val result = runQuery {
        it.executeRead { tx ->
            val r = tx.run(query)
            r.list { record -> record.get("g.name").asString() }
        }
    }

    return result
}