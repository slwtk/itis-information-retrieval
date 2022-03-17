package indexer

import com.github.demidko.aot.WordformMeaning
import opennlp.tools.tokenize.SimpleTokenizer
import java.io.File

fun main() {
  val index: MutableMap<String, Set<String>> = mutableMapOf()
  val file = File("src/main/kotlin/indexer/out/result.txt")
  var result = mutableSetOf<String>()
  file.forEachLine {
    index[it.substringBefore(":")] = it.substringAfter(": ").split(" ").toSet()
  }
  println(
    """
    --------------
    Введите запрос
    --------------
    """.trimIndent()
  )
  val searchLemmas = search(readLine().orEmpty())
  val resultMap = mutableMapOf<String, Set<String>>()
  for ((key, value) in index) if (searchLemmas.contains(key)) resultMap[key] = value

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

  searchLemmas.forEach {
    result =
      if (result.isNotEmpty()) {
        result.intersect(index[it].orEmpty()).toMutableSet()
      } else { index[it].orEmpty().toMutableSet() }
  }
  println("Результат поиска: ${result.joinToString(" ")}")
}

fun search(query: String): Set<String> {
  val tokens = SimpleTokenizer.INSTANCE.tokenize(query.lowercase()).toMutableList()
  val queryLemmas = mutableSetOf<String>()
  tokens.forEach { token ->
    val meanings = WordformMeaning.lookupForMeanings(token)
    if(meanings.isNotEmpty()) {
      queryLemmas.add(meanings[0].lemma.toString())
    }
  }
  return queryLemmas
}