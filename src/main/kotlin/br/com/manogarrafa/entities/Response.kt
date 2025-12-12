package br.com.manogarrafa.entities

import kotlinx.serialization.Serializable

@Serializable
data class RequestList<T>(
    val items: List<T>
)

@Serializable
data class PutDefaultEntityRequest(
    val oldName: String,
    val newName: String
)