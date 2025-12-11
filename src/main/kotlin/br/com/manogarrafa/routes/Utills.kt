package br.com.manogarrafa.routes

import br.com.manogarrafa.database.QueryResult
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun <T> RoutingContext.response(result: QueryResult<T>, successCallback: suspend (T) -> Unit) {
    when (result) {
        is QueryResult.Success<T> -> successCallback(result.result)
        is QueryResult.Error -> call.respond(HttpStatusCode.BadRequest, result.message)
    }
}