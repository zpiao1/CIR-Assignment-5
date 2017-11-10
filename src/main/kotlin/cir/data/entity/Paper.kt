package cir.data.entity

import cir.Fields.AUTHORS
import cir.Fields.ID
import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPER_ABSTRACT
import cir.Fields.PDF_URLS
import cir.Fields.S2_URL
import cir.Fields.TITLE
import cir.Fields.VENUE
import cir.Fields.YEAR
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * InCitations and outCitations will be saved later
 */
@Document
data class Paper(
    val authors: List<String> = listOf(),
    @Id val id: String = "",
    val inCitations: List<String> = listOf(),
    val outCitations: List<String> = listOf(),
    val keyPhrases: List<String> = listOf(),
    val paperAbstract: String = "",
    val pdfUrls: List<String> = listOf(),
    val s2Url: String = "",
    val title: String = "",
    val venue: String = "",
    val year: Int = 0
)

inline fun <reified T> org.bson.Document.getTyped(key: String): T = get(key, T::class.java)

fun <T> org.bson.Document.getList(key: String): List<T> = getTyped(key)

fun <T> org.bson.Document.getArray(key: String): Array<T> = getTyped(key)

fun org.bson.Document.toPaper(): Paper {
  val authors = getList<String>(AUTHORS)
  val id = getString(ID)
  val inCitations = getList<String>(IN_CITATIONS)
  val outCitations = getList<String>(OUT_CITATIONS)
  val keyPhrases = getList<String>(KEY_PHRASES)
  val paperAbstract = getString(PAPER_ABSTRACT)
  val pdfUrls = getList<String>(PDF_URLS)
  val s2Url = getString(S2_URL)
  val title = getString(TITLE)
  val venue = getString(VENUE)
  val year = getInteger(YEAR)
  return Paper(authors, id, inCitations, outCitations, keyPhrases, paperAbstract, pdfUrls, s2Url,
      title, venue, year)
}