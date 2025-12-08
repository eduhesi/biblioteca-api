package br.com.manogarrafa.repositories.collection

fun interface GetAllRepository {
    suspend operator fun invoke(): List<String>
}