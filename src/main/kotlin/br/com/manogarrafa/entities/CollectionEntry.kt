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