package br.com.manogarrafa

import br.com.manogarrafa.repositories.collection.impl.GetAllRepositoryImpl
import br.com.manogarrafa.routes.collection.addCollectionRoute
import br.com.manogarrafa.usecase.collection.GetAll
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<Articles> { article ->
            // Get all articles ...
            val data = GetAll(GetAllRepositoryImpl())()
            call.respond("List of articles sorted starting from ${article.sort}\n\n${data}")
        }
        addCollectionRoute()
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
