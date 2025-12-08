package br.com.manogarrafa.usecase.collection

import br.com.manogarrafa.repositories.collection.GetAllRepository

class GetAll(private val repository: GetAllRepository) {
    suspend operator fun invoke(): List<String> {
        return repository()
    }
}