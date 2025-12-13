package br.com.manogarrafa.database

import br.com.manogarrafa.entities.CollectionResponse

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