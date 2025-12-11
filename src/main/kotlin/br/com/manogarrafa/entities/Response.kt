package br.com.manogarrafa.entities

import kotlinx.serialization.Serializable

@Serializable
data class ResponseList<T>(
    val items: T
)

@Serializable
data class RequestList<T>(
    val items: List<T>
)