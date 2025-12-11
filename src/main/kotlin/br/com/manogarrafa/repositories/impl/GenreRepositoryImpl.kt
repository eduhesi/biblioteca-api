package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.GenreRequest
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

    override suspend fun putGenre(data: GenreRequest): QueryResult<Boolean> {
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

    override suspend fun removeGenre(data: String): QueryResult<Boolean> {
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

}