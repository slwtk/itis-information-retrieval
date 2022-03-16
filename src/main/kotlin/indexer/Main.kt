package indexer

import com.github.demidko.aot.WordformMeaning
import tokenizer.INPUT_PATH
import tokenizer.getTokens
import java.io.File

const val RESULT_FILE = "src/main/kotlin/indexer/out/result.txt"
const val LEMMAS_FILE = "src/main/kotlin/tokenizer/out/lemmas.txt"

fun main() {
  val lemmas = File(LEMMAS_FILE).readLines().map { it.substringBefore(":") }
  val files = File(INPUT_PATH).walkTopDown().filter { it.isFile }.toList()
  val result = mutableMapOf<String, List<String>>()
  val allLemmas = mutableMapOf<String, String>()

  files.forEach { file ->
    val fileLemmas = mutableSetOf<String>()
    file.getTokens().forEach { token ->
      val tokenMeanings = WordformMeaning.lookupForMeanings(token)
      if (tokenMeanings.isNotEmpty()) fileLemmas.add(tokenMeanings[0].lemma.toString())
    }
    allLemmas[file.name] = fileLemmas.joinToString()
    println(fileLemmas)
  }

  lemmas.forEach { lemma ->
    val fileNumbers = mutableListOf<String>()
    for ((key, value) in allLemmas) if (value.contains(lemma)) fileNumbers.add(key)
    result[lemma] = fileNumbers
    println("$lemma ${result[lemma]}")
  }

  File(RESULT_FILE).printWriter().use { out ->
    for((key, value) in result) out.println("$key: ${value.joinToString(" ")}")
  }
}