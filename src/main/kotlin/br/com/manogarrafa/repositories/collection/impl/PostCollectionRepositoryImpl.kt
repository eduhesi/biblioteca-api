package br.com.manogarrafa.repositories.collection.impl

import br.com.manogarrafa.database.Neo4jConnection
import br.com.manogarrafa.entities.AddCollectionRequest
import br.com.manogarrafa.repositories.collection.PostCollectionRepository

class PostCollectionRepositoryImpl() : PostCollectionRepository {

    override suspend fun invoke(request: AddCollectionRequest): Map<String, String> {
        val query =
            "CREATE (c:Collection {name: \$name, publicationYear: \$year, complete: \$complete}) WITH c UNWIND \$authors AS authorName MERGE (a:Author {name: authorName}) MERGE (a)-[:WRITE]->(c) WITH c MERGE (p:Publisher {name: \$publisher}) WITH c UNWIND \$genres AS genreName MERGE (g:Genre {name: genreName}) MERGE (c)-[:HAS_GENRE]->(g)"
        return try {
            Neo4jConnection.session.use { session ->
                session.executeWrite { tx ->
                    tx.run(
                        query,
                        mapOf(
                            "name" to request.collection.name,
                            "year" to request.collection.publicationYear,
                            "complete" to request.collection.complete,
                            "authors" to request.author,
                            "publisher" to request.publisher,
                            "genres" to request.genre
                        )
                    ).consume()
                }
            }
            mapOf("status" to "success")
        } catch (e: Exception) {
            mapOf("status" to "error", "message" to e.localizedMessage.orEmpty())
        }
    }
}