package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult

interface GenreRepository {
    suspend fun getAll(): QueryResult<List<String>>
    suspend fun addGenres(genres: List<String>): QueryResult<Int>
}