package br.com.manogarrafa.database

import org.neo4j.driver.Session

sealed class QueryResult<out T> {
    data class Success<out T>(val result: T) : QueryResult<T>()
    data class Error(val message: String) : QueryResult<Nothing>()
}

fun <T> runQuery(callback: (Session) -> T): QueryResult<T> {
    return try {
        Neo4jConnection.session.use { session ->
            val result = callback(session)
            QueryResult.Success(result)
        }
    } catch (e: Exception) {
        QueryResult.Error(e.localizedMessage.orEmpty())
    }
}