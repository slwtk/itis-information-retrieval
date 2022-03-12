package crawler

import io.thelandscape.krawler.crawler.KrawlConfig
import io.thelandscape.krawler.crawler.Krawler
import io.thelandscape.krawler.http.KrawlDocument
import io.thelandscape.krawler.http.KrawlUrl
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Crawler implementation
 */
class Crawler(config: KrawlConfig = KrawlConfig()) : Krawler() {
  private val filters: Regex = Regex(
    ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|" +
            "mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|tar|ico))$",
    RegexOption.IGNORE_CASE
  )
  val whitelist: MutableSet<String> = ConcurrentSkipListSet()
  var links: MutableList<String> = mutableListOf()

  override fun shouldVisit(url: KrawlUrl): Boolean =
    !filters.matches(url.canonicalForm.split("?").first()) && url.host in whitelist

  override fun visit(url: KrawlUrl, doc: KrawlDocument) {
    if (links.size < 100 && doc.statusCode == 200) links.add(doc.url.canonicalForm)
  }
}
