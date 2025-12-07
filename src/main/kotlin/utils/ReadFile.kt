package utils

import java.io.File

data class Manga(
    val title: String,
    val publicationYear: Int?,
    val publisher: String,
    val price: Double,
    val quantity: Int,
    val editions: MutableList<Edition> = mutableListOf()
)

data class Edition(
    val number: Int?,
    val state: String
)

val mangas = mutableListOf<Manga>()

fun readFile(fileName: String) {
    val fileUrl = object {}.javaClass.classLoader.getResource(fileName)
        ?: run {
            println("Arquivo '$fileName' não encontrado na pasta resources!")
            return
        }

    val file = try {
        File(fileUrl.toURI())
    } catch (e: Exception) {
        println("Erro ao acessar o arquivo '$fileName': ${e.message}")
        return
    }

    val yearRegex = Regex("\\((\\d{4})\\)")

    file.useLines { lines ->
        lines.forEach { line ->
            when {
                line.contains("Valor: R$") -> {
                    val parts = line.split(" /")
                    val titlePublisher = parts.first().trim()

                    val yearMatch = yearRegex.find(titlePublisher)
                    val publicationYear = yearMatch?.groupValues?.get(1)?.toIntOrNull()
                    val cleanTitle = yearMatch?.let { titlePublisher.replace(it.value, "").trim() } ?: titlePublisher

                    val publisher = parts.getOrNull(1)?.substringBefore("Valor:")?.trim() ?: ""
                    val price = parts.last()
                        .substringAfter("R$ ")
                        .substringBefore("\t")
                        .replace(".", "")
                        .replace(",", ".")
                        .toDouble()
                    val quantity = parts.last().substringAfter("Quantidade:").substringBefore("Estado:").trim().toInt()

                    mangas.add(Manga(cleanTitle, publicationYear, publisher, price, quantity))
                }

                line.startsWith(" nº") -> {
                    val number = line.substringAfter(" nº").substringBefore("\t").trim().toIntOrNull()
                    val state = line.substringAfter("Estado:").trim()
                    mangas.lastOrNull()?.editions?.add(Edition(number, state))
                }
            }
        }
    }


}

fun showList() {
    mangas.forEach { manga ->
        println("Título: ${manga.title}")
        println("Ano de Publicação: ${manga.publicationYear ?: "Não informado"}")
        println("Editora: ${manga.publisher}")
        println("Valor: R$ ${"%.2f".format(manga.price)}")
        println("Quantidade: ${manga.quantity}")
        if (manga.editions.isNotEmpty()) {
            println("Edições:")
            manga.editions.forEach { edition ->
                println("  nº: ${edition.number ?: "-"} Estado: ${edition.state}")
            }
        }
        println("---")
    }
}

fun showTotal() {
    var total = 0
    mangas.forEach {
        total += it.quantity
    }

    println(total)
}