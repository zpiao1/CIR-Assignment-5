package cir

import cir.Fields.AUTHORS
import cir.Fields.COUNT
import cir.Fields.DOCUMENT
import cir.Fields.ID
import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPER_ABSTRACT
import cir.Fields.PDF_URLS
import com.mongodb.client.model.Accumulators.sum
import com.mongodb.client.model.Aggregates.lookup
import com.mongodb.client.model.BsonField
import com.mongodb.client.model.Indexes.descending
import com.mongodb.client.model.Projections.include
import com.mongodb.client.model.Sorts.ascending
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults

object Fields {
  val AUTHORS = "authors"
  val ID = "_id"
  val IN_CITATIONS = "inCitations"
  val OUT_CITATIONS = "outCitations"
  val KEY_PHRASES = "keyPhrases"
  val PAPER_ABSTRACT = "paperAbstract"
  val PDF_URLS = "pdfUrls"
  val S2_URL = "s2Url"
  val TITLE = "title"
  val VENUE = "venue"
  val YEAR = "year"
  val COUNT = "count"
  val DOCUMENT = "document"
}

const val COLLECTION = "paper"

val String.isArray
  get() =
    when (this) {
      AUTHORS, IN_CITATIONS, OUT_CITATIONS, KEY_PHRASES, PDF_URLS -> true
      else -> false
    }

fun String.order(asc: Boolean): Bson =
    if (asc) ascending(this) else descending(this)

fun String.toSize() = this + "Size"

fun String.toPath() = "\$" + this

fun String.toField() = if (this == "abstract") PAPER_ABSTRACT else this

fun size(expression: String, `as`: String) = Document(`as`, Document("\$size", expression))

fun addFields(fields: Bson) = Document("\$addFields", fields)

fun includeId(): Bson = include(ID)

fun groupCount(): BsonField = sum(COUNT, 1)

fun lookupPaper(): Bson = lookup(COLLECTION, ID, ID, DOCUMENT)

fun arrayElemAt(path: String, index: Int, `as`: String)
    = Document(`as`, Document("\$arrayElemAt", listOf(path, index)))

fun documentAt0() = arrayElemAt(DOCUMENT.toPath(), 0, DOCUMENT)

fun String.asId() = Document(ID, this)

inline fun <reified T, reified O> MongoOperations.aggregate(aggregation: Aggregation):
    AggregationResults<O> = aggregate(aggregation, T::class.java, O::class.java)
