package indexer

import com.github.demidko.aot.WordformMeaning
import opennlp.tools.tokenize.SimpleTokenizer
import org.jsoup.Jsoup
import tokenizer.INPUT_PATH
import java.io.File
import java.nio.charset.StandardCharsets

const val RESULT_FILE = "src/main/kotlin/indexer/out/result.txt"
const val LEMMAS_FILE = "src/main/kotlin/tokenizer/out/lemmas.txt"

fun main() {
  val lemmas = File(LEMMAS_FILE).readLines().map { it.substringBefore(":") }
  val files = File(INPUT_PATH).walkTopDown().filter { it.isFile }.toList()
  val regex = "[^\\u0400-\\u04FF]+$".toRegex()
  // word exceptions
  val stopWords = setOf(
    "в", "без", "до", "из", "к", "на", "по", "о", "от", "перед",
    "при", "через", "с", "со", "у", "и", "нет", "за", "над", "для", "об",
    "под", "про", "когда", "пока", "едва", "лишь", "только", "потому", "что",
    "так", "как", "оттого", "что", "ибо", "чтобы", "чтоб", "для", "того",
    "чтобы", "с тем чтобы", "если", "раз", "бы", "а", "но", "и"
  )

  val result = mutableMapOf<String, List<String>>()
  val lemmasFromFiles = mutableMapOf<String, String>()

  files.forEach { file ->
    val html = Jsoup.parse(file, StandardCharsets.UTF_8.toString())
    val cyrillicWords = SimpleTokenizer.INSTANCE.tokenize(html.text().lowercase()).toMutableList()
    val fileLemmas = mutableListOf<String>()
    cyrillicWords.removeIf { it.matches(regex) || it.length > 15 || it.length <= 2 || stopWords.contains(it)}
    cyrillicWords.forEach {
      val mean = WordformMeaning.lookupForMeanings(it)
      if (mean.size != 0) fileLemmas.add(mean[0].lemma.toString())
    }
    lemmasFromFiles[file.name] = fileLemmas.joinToString()
  }

  lemmas.forEach { lemma ->
    val values = mutableListOf<String>()
    for ((key, value) in lemmasFromFiles) if (value.contains(lemma)) values.add(key)
    result[lemma] = values
    println("$lemma ${result[lemma]}")
  }

  File(RESULT_FILE).printWriter().use { out ->
    for((key, value) in result) out.println("$key: ${value.joinToString(" ")}")
  }
}