package cir.api.service

import cir.*
import cir.Fields.AUTHORS
import cir.Fields.COUNT
import cir.Fields.DOCUMENT
import cir.Fields.ID
import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPERS
import cir.Fields.VENUE
import cir.Fields.YEAR
import cir.data.entity.getTyped
import cir.data.entity.toPaper
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Service

@Service
class KeyPhrasesServiceImpl
@Autowired constructor(
    mongoOperations: MongoOperations
) : KeyPhrasesService {

  private val collection = mongoOperations.getCollection(COLLECTION)

  private fun keyPhraseEq(keyPhrase: String) = eq(KEY_PHRASES, keyPhrase)

  override fun doesKeyPhraseExist(keyPhrase: String) = countPapersByKeyPhrase(keyPhrase) > 0

  override fun countKeyPhrases() =
      collection.aggregate(listOf(
          unwind(KEY_PHRASES.toPath()),
          group(KEY_PHRASES.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()

  override fun countByKeyPhrase(
      keyPhrase: String,
      field: String
  ) = when (field) {
    PAPERS -> countPapersByKeyPhrase(keyPhrase)
    AUTHORS, VENUE, YEAR, IN_CITATIONS, OUT_CITATIONS -> countFieldByKeyPhrase(keyPhrase, field)
    else -> 0L
  }

  private fun countPapersByKeyPhrase(keyPhrase: String) = collection.count(keyPhraseEq(keyPhrase))

  private fun countFieldByKeyPhrase(keyPhrase: String, field: String) =
      if (field.isArray) countArrayFieldByKeyPhrase(keyPhrase, field)
      else countSingularFieldByKeyPhrase(keyPhrase, field)

  private fun countArrayFieldByKeyPhrase(keyPhrase: String, field: String) =
      collection.aggregate(listOf(
          unwind(field.toPath()),
          match(keyPhraseEq(keyPhrase)),
          group(field.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()

  private fun countSingularFieldByKeyPhrase(keyPhrase: String, field: String) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          group(field.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()

  override fun findKeyPhrases(asc: Boolean, limit: Int, orderBy: String) =
      when (orderBy) {
        "keyPhrase" -> findKeyPhrasesOrderByKeyPhrase(asc, limit)
        "papers" -> findKeyPhrasesOrderByPapers(asc, limit)
        else -> Any()
      }

  private fun findKeyPhrasesOrderByKeyPhrase(asc: Boolean, limit: Int) =
      collection.aggregate(listOf(
          unwind(KEY_PHRASES.toPath()),
          group(KEY_PHRASES.toPath()),
          sort(KEY_PHRASES.order(asc)),
          limit(limit),
          project(includeId())
      ))
          .map { it.getString(ID) }
          .toList()

  private fun findKeyPhrasesOrderByPapers(asc: Boolean, limit: Int) =
      collection.aggregate(listOf(
          unwind(KEY_PHRASES.toPath()),
          group(KEY_PHRASES.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  override fun findByKeyPhrase(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      when (field) {
        PAPERS -> findPapersByKeyPhrase(keyPhrase, asc, limit, orderBy)
        AUTHORS -> findArrayFieldByKeyPhrase(keyPhrase, asc, limit, field, orderBy)
        VENUE -> findSingularFieldsByKeyPhrase(keyPhrase, asc, limit, field, orderBy)
        YEAR -> findYearsByKeyPhrase(keyPhrase, asc, limit, orderBy)
        IN_CITATIONS, OUT_CITATIONS ->
          findCitationsByKeyPhrase(keyPhrase, asc, limit, field, orderBy)
        else -> Any()
      }

  private fun findPapersByKeyPhrase(keyPhrase: String, asc: Boolean, limit: Int, orderBy: String) =
      if (orderBy.isArray) findPapersByKeyPhraseOrderByArrayField(keyPhrase, asc, limit, orderBy)
      else findPapersByKeyPhraseOrderBySingularField(keyPhrase, asc, limit, orderBy)

  private fun findPapersByKeyPhraseOrderByArrayField(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          addFields(size(field.toPath(), field.toSize())),
          sort(field.toSize().order(asc)),
          project(exclude(field.toSize())),
          limit(limit)
      ))
          .map { it.toPaper() }
          .toList()

  private fun findPapersByKeyPhraseOrderBySingularField(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      orderBy: String
  ) =
      collection.find(keyPhraseEq(keyPhrase))
          .sort(orderBy.order(asc))
          .limit(limit)
          .map { it.toPaper() }
          .toList()

  private fun findArrayFieldByKeyPhrase(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      when (orderBy) {
        "papers" -> findArrayFieldsByKeyPhraseOrderByPapers(keyPhrase, asc, limit, field)
        "name" -> findArrayFieldsByKeyPhraseOrderByArrayField(keyPhrase, asc, limit, field)
        else -> Any()
      }

  private fun findArrayFieldsByKeyPhraseOrderByPapers(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          unwind(field.toPath()),
          group(field.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  private fun findArrayFieldsByKeyPhraseOrderByArrayField(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          unwind(field.toPath()),
          group(field.toPath()),
          sort(ID.order(asc)),
          limit(limit)
      ))
          .map { it.getString(ID) }
          .toList()

  private fun findSingularFieldsByKeyPhrase(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      when (orderBy) {
        "papers" -> findSingularFieldsByKeyPhraseOrderByPapers(keyPhrase, asc, limit, field)
        "venue" -> findSingularFieldsByKeyPhraseOrderBySingularField(keyPhrase, asc, limit, field)
        else -> Any()
      }

  private fun findSingularFieldsByKeyPhraseOrderByPapers(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          group(field.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  private fun findSingularFieldsByKeyPhraseOrderBySingularField(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          group(field.toPath()),
          sort(ID.order(asc)),
          limit(limit)
      ))
          .map { it.getString(ID) }
          .toList()

  private fun findYearsByKeyPhrase(keyPhrase: String, asc: Boolean, limit: Int, orderBy: String) =
      when (orderBy) {
        "papers" -> findYearsByKeyPhraseOrderByPapers(keyPhrase, asc, limit)
        "year" -> findYearsByKeyPhraseOrderByYear(keyPhrase, asc, limit)
        else -> Any()
      }

  private fun findYearsByKeyPhraseOrderByPapers(keyPhrase: String, asc: Boolean, limit: Int) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          group(YEAR.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(linkedMapOf(), { it.getInteger(ID) }, { it.getInteger(COUNT) })

  private fun findYearsByKeyPhraseOrderByYear(keyPhrase: String, asc: Boolean, limit: Int) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          group(YEAR.toPath()),
          sort(ID.order(asc)),
          limit(limit)
      ))
          .map { it.getInteger(ID) }
          .toList()

  private fun findCitationsByKeyPhrase(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      if (orderBy.isArray) {
        findCitationsByKeyPhraseOrderByArrayField(keyPhrase, asc, limit, field, orderBy)
      } else {
        findCitationsByKeyPhraseOrderBySingularField(keyPhrase, asc, limit, field, orderBy)
      }

  private fun findCitationsByKeyPhraseOrderByArrayField(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          unwind(field.toPath()),
          group(field.toPath()),
          lookupPaper(),
          project(documentAt0()),
          project(fields(include(DOCUMENT), size("$DOCUMENT.$orderBy".toPath(), orderBy.toSize()))),
          sort(orderBy.toSize().order(asc)),
          limit(limit)
      ))
          .map { it.getTyped<Document>(DOCUMENT).toPaper() }
          .toList()

  private fun findCitationsByKeyPhraseOrderBySingularField(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      collection.aggregate(listOf(
          match(keyPhraseEq(keyPhrase)),
          unwind(field.toPath()),
          group(field.toPath()),
          lookupPaper(),
          project(documentAt0()),
          sort("$DOCUMENT.$orderBy".order(asc)),
          limit(limit)
      ))
          .map { it.getTyped<Document>(DOCUMENT).toPaper() }
          .toList()
}