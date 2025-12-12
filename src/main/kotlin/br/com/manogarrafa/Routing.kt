package br.com.manogarrafa

import br.com.manogarrafa.routes.authorRoutes
import br.com.manogarrafa.routes.collectionRoutes
import br.com.manogarrafa.routes.genreRoutes
import io.ktor.resources.*
import io.ktor.server.application.*
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
        collectionRoutes()
        genreRoutes()
        authorRoutes()
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
