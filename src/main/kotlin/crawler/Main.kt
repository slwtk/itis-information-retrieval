package crawler

import io.thelandscape.krawler.crawler.KrawlConfig
import org.jsoup.Jsoup
import java.io.File

const val INDEX_FILE_PATH = "src/main/kotlin/crawler/index.txt"
const val PAGES_DIR_PATH = "src/main/kotlin/crawler/out/"
const val SITE = "habr.com"
const val SITE_URL = "https://${SITE}/ru/all"

fun main() {
  val k = Crawler(KrawlConfig())
  k.whitelist.addAll(listOf(SITE)).also { k.startNonblocking(SITE_URL) }
  while (k.links.size < 100) Thread.sleep(3000)
  k.stop()

  File(INDEX_FILE_PATH).printWriter().use { out ->
    k.links.forEach { out.println(it) }
  }

  k.links.forEachIndexed { i, e ->
    File(PAGES_DIR_PATH + i).writeText(Jsoup.connect(e).get().html())
  }
}