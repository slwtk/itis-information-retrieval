package tokenizer

import opennlp.tools.tokenize.SimpleTokenizer
import org.jsoup.Jsoup
import com.github.demidko.aot.WordformMeaning
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.HashMap
import kotlin.collections.HashSet

class Tokenizer {
  private val regex = "[^\\u0400-\\u04FF]+$".toRegex()
  private val stopWords = setOf(
    "в", "без", "до", "из", "к", "на", "по", "о", "от", "перед",
    "при", "через", "с", "со", "у", "и", "нет", "за", "над", "для", "об",
    "под", "про", "когда", "пока", "едва", "лишь", "только", "потому", "что",
    "так", "как", "оттого", "что", "ибо", "чтобы", "чтоб", "для", "того",
    "чтобы", "с тем чтобы", "если", "раз", "бы", "а", "но", "и"
  )

  fun getTokens(line: String): Set<String> {
    val tokens: MutableSet<String> = HashSet()
    val cyrillicWords = SimpleTokenizer.INSTANCE.tokenize(line.lowercase()).toMutableList()
    cyrillicWords.removeIf { it.matches(regex) || it.length > 15 || it.length <= 2 || stopWords.contains(it)}
    tokens.addAll(cyrillicWords)
    return tokens
  }

  fun getTokens(file: File): Set<String> = getTokens(Jsoup.parse(file, StandardCharsets.UTF_8.toString()).text())

  fun getLemma(token: String): String? {
    val meanings = WordformMeaning.lookupForMeanings(token)
    return if (meanings.isNotEmpty()) meanings[0].lemma.toString() else null
  }

  fun groupByLemmas(tokens: Set<String>): Map<String, Set<String>> {
    val result: MutableMap<String, MutableSet<String>> = HashMap()
    val notFoundWords: MutableSet<String> = HashSet()
    tokens.forEach { token ->
      val lemma = token.getLemma()
      if (lemma != null) {
        if (result.containsKey(lemma)) {
          result[lemma]?.add(token)
        } else {
          result[lemma] = mutableSetOf(token)
        }
      } else {
        notFoundWords.add(token)
      }
    }
    result["Tokens without lemma"] = notFoundWords
    return result
  }
}

fun File.getTokens(): Set<String> = Tokenizer().getTokens(this)

fun String.getTokens(): Set<String> = Tokenizer().getTokens(this)

fun String.getLemma(): String? = Tokenizer().getLemma(this)