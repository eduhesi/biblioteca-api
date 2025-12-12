package br.com.manogarrafa.entities

import kotlinx.serialization.Serializable

@Serializable
data class CollectionData(
    val name: String,
    val publicationYear: Int,
    val complete: Boolean
)

@Serializable
data class AddCollectionRequest(
    val collection: CollectionData,
    val author: List<String>,
    val publisher: String,
    val genre: List<String>
)

@Serializable
data class CollectionResponse(
    val name: String,
    val cover: String,
    val publicationYear: Int,
    val totalEditions: Int
)

@Serializable
data class EditionRequest(
    val name: String,
    val cover: String,
    val number: Int,
    val price: Double,
    val status: String,
    val quantity: Int
)
