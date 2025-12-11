package br.com.manogarrafa.entities

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val name: String
)

@Serializable
data class GenreRequest(
    val oldName: String,
    val newName: String
)