package tokenizer

import opennlp.tools.tokenize.SimpleTokenizer
import org.jsoup.Jsoup
import com.github.demidko.aot.WordformMeaning
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

class Tokenizer(private val inputPath: String, private val outputPath: String) {
  fun tokenize() {
    val tokens: MutableSet<String> = HashSet()
    val regex = "[^\\u0400-\\u04FF]+$".toRegex()
    // word exceptions
    val stopWords = setOf(
      "в", "без", "до", "из", "к", "на", "по", "о", "от", "перед",
      "при", "через", "с", "со", "у", "и", "нет", "за", "над", "для", "об",
      "под", "про", "когда", "пока", "едва", "лишь", "только", "потому", "что",
      "так", "как", "оттого", "что", "ибо", "чтобы", "чтоб", "для", "того",
      "чтобы", "с тем чтобы", "если", "раз", "бы", "а", "но", "и"
    )

    File(inputPath).walkTopDown().filter { it.isFile }.toList().forEach { file ->
      println("${file.absolutePath} parsing...")
      // parsing html
      val html = Jsoup.parse(file, StandardCharsets.UTF_8.toString())
      val cyrillicWords = SimpleTokenizer.INSTANCE.tokenize(html.text().lowercase()).toMutableList()
      cyrillicWords.removeIf { it.matches(regex) || it.length > 15 || it.length <= 2 || stopWords.contains(it)}
      // tokenizing html
      tokens.addAll(cyrillicWords)
      println("Tokens list has ${tokens.size} items")
    }

    // writing in tokens.txt
    println("Writing result to $outputPath/tokens.txt")
    File("$outputPath/tokens.txt").printWriter().use { out -> tokens.forEach { out.println(it) } }
    println("Done")

    // writing in lemmas.txt
    println("Writing result to $outputPath/lemmas.txt")
    File("$outputPath/lemmas.txt").printWriter().use { out ->
      groupByLemmas(tokens).forEach { (key, value) -> out.println("$key: $value") }
    }
    println("Done")
  }

  private fun groupByLemmas(tokens: Set<String>): Map<String, String> {
    val result: MutableMap<String, String> = HashMap()
    val notFoundWords: MutableList<String> = ArrayList()
    tokens.forEach { token ->
      val meanings = WordformMeaning.lookupForMeanings(token)
      if (meanings.size != 0) {
        val key = meanings[0].lemma.toString()
        if (result.containsKey(key)) result[key] += " $token" else result[key] = token
      } else {
        notFoundWords.add(token)
      }
    }
    result["Tokens without lemma"] = notFoundWords.joinToString(" ")
    return result
  }
}