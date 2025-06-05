package br.com.manogarrafa.utils

import java.io.File

data class Manga(
    val title: String,
    val publicationYear: Int?, // Adicionado campo para o ano de publicação
    val publisher: String,
    val price: Double,
    val quantity: Int,
    var editions: List<Edition> = emptyList() // Mudado para 'var' para permitir adição de edições
)

data class Edition(
    val number: Int?,
    val state: String
)

fun readFile() {
    val fileName = "colecao_estrangeira_completa.txt" // Apenas o nome do arquivo, pois está em 'resources'

    // Tenta obter o URL do recurso usando o Classloader
    val fileUrl = object {}.javaClass.classLoader.getResource(fileName)

    if (fileUrl == null) {
        println("Arquivo '$fileName' não encontrado na pasta resources!")
        return
    }

    val file: File
    try {
        // Converte o URL para um objeto File
        file = File(fileUrl.toURI())
    } catch (e: Exception) {
        // Lidar com possíveis erros na conversão da URI (ex: URI malformada)
        println("Erro ao acessar o arquivo '$fileName': ${e.message}")
        return
    }

    val mangas = mutableListOf<Manga>()
    var currentManga: Manga? = null

    // Expressão regular para encontrar o ano entre parênteses
    val yearRegex = Regex("\\((\\d{4})\\)")

    file.forEachLine { line ->
        // Verifica se a linha contém informações do título do mangá
        if (line.contains("Valor: R$")) {
            val parts = line.split(" /")
            val titlePublisher = parts[0].trim()

            // Extrair o ano de publicação
            val yearMatch = yearRegex.find(titlePublisher)
            val publicationYear = yearMatch?.groupValues?.get(1)?.toIntOrNull()

            // Remover o ano e os parênteses do título para ter o título limpo
            val cleanTitle = yearMatch?.let {
                titlePublisher.replace(it.value, "").trim()
            } ?: titlePublisher

            val publisher = parts.getOrNull(1)?.substringBefore("Valor:")?.trim() ?: ""
            val price = parts.last().substringAfter("R$ ").substringBefore(" Quantidade:").replace(",", ".").toDouble()
            val quantity = parts.last().substringAfter("Quantidade:").substringBefore("Estado:").trim().toInt()

            currentManga = Manga(cleanTitle, publicationYear, publisher, price, quantity)
            mangas.add(currentManga!!) // '!!' usado porque currentManga acabou de ser atribuído
        } else if (line.startsWith("nº")) {
            // Extrai as informações da edição
            val number = line.substringAfter("nº").substringBefore(" Estado:").trim().toIntOrNull()
            val state = line.substringAfter("Estado:").trim()
            currentManga?.let {
                it.editions = it.editions + Edition(number, state)
            }
        }
    }

    // Imprime os dados dos mangás
    mangas.forEach { manga ->
        println("Título: ${manga.title}")
        println("Ano de Publicação: ${manga.publicationYear ?: "Não informado"}") // Imprime o ano
        println("Editora: ${manga.publisher}")
        println("Valor: R$ ${"%.2f".format(manga.price)}") // Formata o valor para 2 casas decimais
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