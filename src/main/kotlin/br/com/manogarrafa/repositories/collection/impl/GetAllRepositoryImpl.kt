package br.com.manogarrafa.repositories.collection.impl

import br.com.manogarrafa.database.Neo4jConnection
import br.com.manogarrafa.repositories.collection.GetAllRepository

class GetAllRepositoryImpl : GetAllRepository {
    override suspend fun invoke(): List<String> {
        val resultList = Neo4jConnection.session.executeRead { tx ->
            val result = tx.run("MATCH (c:Collection) RETURN c.name")
            result.list { record -> record.get("c.name").asString() } // Consome aqui!
        }
        Neo4jConnection.session.close()
        return resultList
    }
}