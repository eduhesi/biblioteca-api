package br.com.manogarrafa.repositories.collection.impl

import br.com.manogarrafa.database.Neo4jConnection
import br.com.manogarrafa.repositories.collection.GetAllRepository

class GetAllRepositoryImpl : GetAllRepository {
    override suspend fun invoke(): List<String> {
        val query = "MATCH (c:Collection) RETURN c.name"
        val result = Neo4jConnection.session.run(query)
        val names = result.list { it.get("c.name").asString() }
        return names
    }
}