package br.com.manogarrafa.repositories

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.entities.GenreRequest

interface GenreRepository {
    suspend fun getAll(): QueryResult<List<String>>
    suspend fun addGenres(genres: List<String>): QueryResult<Int>
    suspend fun putGenre(data: GenreRequest): QueryResult<Boolean>
    suspend fun removeGenre(data: String): QueryResult<Boolean>
}