package br.com.manogarrafa.entities

import kotlinx.serialization.Serializable

enum class EditionStatus {
    READING,
    READ,
    PLAN_TO_READ,
    ON_HOLD,
    DROPPED
}

@Serializable
data class CollectionEditionsResponse(
    val collection: CollectionData,
    val authors: List<String>,
    val genres: List<String>,
    val editions: List<EditionResponse>
)

@Serializable
data class EditionResponse(
    val cover: String,
    val number: Int,
    val quantity: Int,
    val price: Double,
    val publisher: String,
    val status: String
)