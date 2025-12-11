package br.com.manogarrafa.database

import com.typesafe.config.ConfigFactory
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session

object Neo4jConnection {
    private val config = ConfigFactory.load()
    private val neo4jConfig = config.getConfig("neo4j")
    private val NEO4J_URI = neo4jConfig.getString("uri")
    private val NEO4J_USER = neo4jConfig.getString("user")
    private val NEO4J_PASSWORD = neo4jConfig.getString("password")

    private val driver: Driver by lazy {
        GraphDatabase.driver(
            NEO4J_URI,
            AuthTokens.basic(NEO4J_USER, NEO4J_PASSWORD)
        )
    }

    fun close() {
        driver.close()
    }

    val session: Session
        get() = driver.session()
}