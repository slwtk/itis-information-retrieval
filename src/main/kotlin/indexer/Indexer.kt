package indexer

import tokenizer.INPUT_PATH
import tokenizer.getLemma
import tokenizer.getTokens
import java.io.File

const val INVERTED_INDEX_FILE = "src/main/kotlin/indexer/out/inverted_index.txt"
const val LEMMAS_FILE = "src/main/kotlin/tokenizer/out/lemmas.txt"

fun main() {
  val files = File(INPUT_PATH).walkTopDown().filter { it.isFile }.toList()
  val allFileLemmas = mutableMapOf<String, String>()
  files.forEach { file ->
    val fileLemmas = buildList {
      file.getTokens().map { token ->
        token.getLemma()?.let { lemma -> add(lemma) }
      }
    }
    allFileLemmas[file.name] = fileLemmas.joinToString()
    println("${file.name}: $fileLemmas")
  }

  val lemmas = File(LEMMAS_FILE).readLines().map { file ->
    file.substringBefore(":")
  }
  val result = mutableMapOf<String, Set<String>>()

  lemmas.forEach { lemma ->
    val fileNumbers = mutableSetOf<String>()
    for ((key, value) in allFileLemmas) if (value.contains(lemma)) fileNumbers.add(key)
    result[lemma] = fileNumbers
    println("$lemma ${result[lemma]}")
  }

  File(INVERTED_INDEX_FILE).printWriter().use { out ->
    for((key, value) in result) out.println("$key: ${value.joinToString(" ")}")
  }
}