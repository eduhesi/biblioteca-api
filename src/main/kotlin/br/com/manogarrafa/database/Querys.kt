package br.com.manogarrafa.database

fun getCollectionsBy(match: String, param: String): Pair<String, Map<String, String>> {
    val query = $$"""
        $$match
        WHERE t.name = $tagName
        WITH c, collect(e) AS editions
        RETURN
            c.name AS collectionName,
            editions[0].cover AS firstEditionCover,
            c.publicationYear AS year,
            reduce(total = 0, ed IN editions | total + coalesce(ed.quantity, 0)) AS totalEditions
        ORDER BY collectionName
        """.trimIndent()

    val params = mapOf("tagName" to param)

    return query to params
}