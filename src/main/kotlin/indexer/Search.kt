package indexer

import tokenizer.getLemma
import java.io.File

const val LEMMA_TOKENS_FILE = "src/main/kotlin/indexer/out/result.txt"

fun main() {
  println(
    """
    --------------
    Введите запрос
    --------------
    """.trimIndent()
  )

  val searchQueryLemmas = readLine().orEmpty().split(" ").map { word -> word.getLemma() }.toSet()

  val fileLemmaTokens = mutableMapOf<String, Set<String>>()
  File(LEMMA_TOKENS_FILE).forEachLine { line ->
    fileLemmaTokens[line.substringBefore(":")] =
      line.substringAfter(": ").split(" ").toSet()
  }

  val resultMap = mutableMapOf<String, Set<String>>()
  for ((key, value) in fileLemmaTokens) if (searchQueryLemmas.contains(key)) resultMap[key] = value

  println("Поиск...")
  for ((key,value) in resultMap) {
    println(
      """
      ------------
      $key -> $value
      ------------
      """.trimIndent()
    )
  }

  var searchResult: Set<String> = fileLemmaTokens[searchQueryLemmas.first()].orEmpty()
  searchQueryLemmas.forEach {
    searchResult = searchResult.intersect(fileLemmaTokens[it].orEmpty())
  }

  println("Результат поиска: ${searchResult.joinToString(" ")}")
}