package indexer

import crawler.PAGES_DIR_PATH
import tokenizer.getLemma
import java.io.File

fun main() {
  println(
    """
    --------------
    Введите запрос
    --------------
    """.trimIndent()
  )

  val searchQueryLemmas = readLine().orEmpty().split(" ").map { word ->
    word.getLemma()
  }.toSet()

  val fileLemmaTokens = buildMap {
    File(INVERTED_INDEX_FILE).forEachLine { line ->
      this[line.substringBefore(":")] =
        line.substringAfter(": ").split(" ").toSet()
    }
  }

  val resultMap = buildMap {
    for ((key, value) in fileLemmaTokens) if (searchQueryLemmas.contains(key)) this[key] = value
  }

  println("--------------")
  for ((key, value) in resultMap) println("$key: ${value.joinToString(" ")}")
  println("--------------")

  var searchResultIntersect = fileLemmaTokens[searchQueryLemmas.first()].orEmpty()
  var searchResultUnion = fileLemmaTokens[searchQueryLemmas.first()].orEmpty()

  searchQueryLemmas.forEach {
    searchResultIntersect = searchResultIntersect.intersect(fileLemmaTokens[it].orEmpty())
    searchResultUnion = searchResultUnion.union(fileLemmaTokens[it].orEmpty())
  }

  println(
    """
    Результат поиска AND: 
    ${searchResultIntersect.joinToString(separator = " ") { PAGES_DIR_PATH + it }.ifBlank { "Ничего не найдено" }}
    """.trimIndent()
  )
  println(
    """
    Результат поиска OR: 
    ${searchResultUnion.joinToString(separator = " ", limit = 3) { PAGES_DIR_PATH + it }.ifBlank { "Ничего не найдено" }}
    """.trimIndent()
  )
}
