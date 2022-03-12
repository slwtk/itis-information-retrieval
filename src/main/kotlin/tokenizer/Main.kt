package tokenizer

import java.io.File

const val INPUT_PATH = "src/main/kotlin/crawler/out"
const val OUTPUT_PATH = "src/main/kotlin/tokenizer/out"

fun main() {
  val tokenizer = Tokenizer()
  val files = File(INPUT_PATH).walkTopDown().filter { it.isFile }.toList()
  val tokens = tokenizer.tokenize(files)

  // writing in tokens.txt
  println("Writing result to $OUTPUT_PATH/tokens.txt")
  File("$OUTPUT_PATH/tokens.txt").printWriter().use { out -> tokens.forEach { out.println(it) } }
  println("Done")

  // writing in lemmas.txt
  println("Writing result to $OUTPUT_PATH/lemmas.txt")
  File("$OUTPUT_PATH/lemmas.txt").printWriter().use { out ->
    tokenizer.groupByLemmas(tokens).forEach { (key, value) -> out.println("$key: $value") }
  }
  println("Done")
}