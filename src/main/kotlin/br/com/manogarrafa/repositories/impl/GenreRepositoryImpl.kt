package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.repositories.GenreRepository

class GenreRepositoryImpl : GenreRepository {
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

    override suspend fun addGenres(genres: List<String>): QueryResult<Int> {
        val query = $$"""
        UNWIND $genres AS genreName
            MERGE (g:Genre {name: genreName})
        """.trimIndent()

        val result = runQuery {
            val summary = it.executeWrite { tx ->
                tx.run(query, mapOf("genres" to genres)).consume()
            }
            summary.counters().nodesCreated()
        }

        return result
    }
}