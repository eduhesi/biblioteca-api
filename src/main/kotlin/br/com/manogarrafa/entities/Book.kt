package br.com.manogarrafa.entities

data class Book(
    val name: String,
    val cover: String,
    val publisher: Publisher,
    val authors: List<Author>,
    val releaseDate: String,
    val price: Double,
    val language: Language
)