package br.com.manogarrafa.repositories.impl

import br.com.manogarrafa.database.QueryResult
import br.com.manogarrafa.database.runQuery
import br.com.manogarrafa.entities.CollectionData
import br.com.manogarrafa.entities.CollectionEditionsResponse
import br.com.manogarrafa.entities.EditionResponse
import br.com.manogarrafa.repositories.EditionRepository

class EditionRepositoryImpl : EditionRepository {
    override suspend fun getAllEditionsFromCollection(collection: String): QueryResult<CollectionEditionsResponse> {
        val query = $$"""
        MATCH (c:Collection {name: $collectionName})
        OPTIONAL MATCH (a:Author)-[:WRITE]->(c)
        OPTIONAL MATCH (c)-[:HAS_GENRE]->(g:Genre)
        OPTIONAL MATCH (p:Publisher)-[e:EDITION]->(c)
        WITH c, 
             collect(DISTINCT a.name) AS authors, 
             collect(DISTINCT g.name) AS genres,
             collect(DISTINCT {
                cover: e.cover, 
                number: e.number, 
                quantity: e.quantity, 
                price: e.price, 
                status: e.status, 
                publisher: p.name
             }) AS editions
        RETURN 
          c.name AS collectionName,
          c.publicationYear AS publicationYear,
          c.complete AS complete,
          authors,
          genres,
          editions
        """.trimIndent()
        val params = mapOf("collectionName" to collection)

        return runQuery {
            it.executeRead { tx ->
                val result = tx.run(query, params)
                result.single().let { record ->
                    CollectionEditionsResponse(
                        collection = CollectionData(
                            name = record.get("collectionName").asString(),
                            publicationYear = record.get("publicationYear").asInt(),
                            complete = record.get("complete").asBoolean()
                        ),
                        authors = record.get("authors").asList { a -> a.asString() },
                        genres = record.get("genres").asList { g -> g.asString() },
                        editions = record.get("editions").asList { ed ->
                            EditionResponse(
                                cover = ed.get("cover").asString(),
                                number = ed.get("number").asInt(),
                                quantity = ed.get("quantity").asInt(),
                                price = ed.get("price").asDouble(),
                                publisher = ed.get("publisher").asString(),
                                status = ed.get("status").asString()
                            )
                        }
                    )
                }
            }
        }
    }

    override suspend fun getAllEditionsFromCollectionByPublisher(
        collection: String,
        publisher: String
    ): QueryResult<CollectionEditionsResponse> {
        val query = $$"""
        MATCH (c:Collection {name: $collectionName})
        OPTIONAL MATCH (a:Author)-[:WRITE]->(c)
        OPTIONAL MATCH (c)-[:HAS_GENRE]->(g:Genre)
        MATCH (p:Publisher {name: $publisherName})-[e:EDITION]->(c)
        WITH c, 
             collect(DISTINCT a.name) AS authors, 
             collect(DISTINCT g.name) AS genres,
             collect(DISTINCT {
                cover: e.cover, 
                number: e.number, 
                quantity: e.quantity, 
                price: e.price, 
                status: e.status, 
                publisher: p.name
             }) AS editions
        RETURN 
          c.name AS collectionName,
          c.publicationYear AS publicationYear,
          c.complete AS complete,
          authors,
          genres,
          editions
        """.trimIndent()
        val params = mapOf("collectionName" to collection, "publisherName" to publisher)

        return runQuery {
            it.executeRead { tx ->
                val result = tx.run(query, params)
                result.single().let { record ->
                    CollectionEditionsResponse(
                        collection = CollectionData(
                            name = record.get("collectionName").asString(),
                            publicationYear = record.get("publicationYear").asInt(),
                            complete = record.get("complete").asBoolean()
                        ),
                        authors = record.get("authors").asList { a -> a.asString() },
                        genres = record.get("genres").asList { g -> g.asString() },
                        editions = record.get("editions").asList { ed ->
                            EditionResponse(
                                cover = ed.get("cover").asString(),
                                number = ed.get("number").asInt(),
                                quantity = ed.get("quantity").asInt(),
                                price = ed.get("price").asDouble(),
                                publisher = ed.get("publisher").asString(),
                                status = ed.get("status").asString()
                            )
                        }
                    )
                }
            }
        }
    }
}