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
import cir.data.entity.Paper
import cir.data.entity.getTyped
import cir.data.entity.toPaper
import com.mongodb.client.AggregateIterable
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.regex
import com.mongodb.client.model.Projections.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Service
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

@Service
class AuthorsServiceImpl
@Autowired constructor(
    mongoOperations: MongoOperations
) : AuthorsService {

  private val collection = mongoOperations.getCollection(COLLECTION)

  private fun nameContains(nameContains: String) =
      regex(AUTHORS, Pattern.compile(".*$nameContains.*", CASE_INSENSITIVE))

  private fun nameEq(name: String) = eq(AUTHORS, name)

  override fun doesAuthorExist(author: String) = countPapersByAuthor(author) > 0

  override fun countAuthors() =
      collection.aggregate(listOf(
          unwind(AUTHORS.toPath()),
          group(AUTHORS.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()

  override fun countAuthorsByNameContains(nameContains: String) =
      collection.aggregate(listOf(
          unwind(AUTHORS.toPath()),
          match(nameContains(nameContains)),
          group(AUTHORS.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()

  override fun countByAuthor(author: String, field: String) =
      when (field) {
        PAPERS -> countPapersByAuthor(author)
        KEY_PHRASES, VENUE, YEAR, IN_CITATIONS, OUT_CITATIONS -> countFieldByAuthor(author, field)
        else -> 0L
      }

  private fun countPapersByAuthor(author: String) = collection.count(nameEq(author))

  private fun countFieldByAuthor(author: String, field: String) =
      if (field.isArray) countWithUnwinding(author, field)
      else countWithoutUnwinding(author, field)

  private fun countByAuthorAggregates(author: String, field: String) =
      listOf(
          match(nameEq(author)),
          group(field.toPath()),
          count()
      )

  private fun countWithUnwinding(author: String, field: String): Long {
    val list = mutableListOf(unwind(field.toPath()))
    list.addAll(countByAuthorAggregates(author, field))
    return collection.aggregate(list)
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  private fun countWithoutUnwinding(author: String, field: String): Long =
      collection.aggregate(countByAuthorAggregates(author, field))
          .first()
          .getInteger(COUNT)
          .toLong()

  override fun findAuthors(asc: Boolean, limit: Int, orderBy: String) =
      when (orderBy) {
        "name" -> findAuthorsOrderByName(asc, limit)
        "papers" -> findAuthorsOrderByPapers(asc, limit)
        else -> Any()
      }

  private fun findAuthorsOrderByName(asc: Boolean, limit: Int): List<String> {
    return collection.aggregate(listOf(
        unwind(AUTHORS.toPath()),
        group(AUTHORS.toPath()),
        sort(AUTHORS.order(asc)),
        limit(limit),
        project(includeId())
    ))
        .map { it.getString(ID) }
        .toList()
  }

  private fun findAuthorsOrderByPapers(asc: Boolean, limit: Int) =
      collection.aggregate(listOf(
          unwind(AUTHORS.toPath()),
          group(AUTHORS.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  override fun findAuthorsByNameContains(
      nameContains: String,
      asc: Boolean,
      limit: Int,
      orderBy: String
  ) =
      when (orderBy) {
        "name" -> findAuthorsByNameContainsOrderByName(nameContains, asc, limit)
        "papers" -> findAuthorsByNameContainsOrderByPapers(nameContains, asc, limit)
        else -> Any()
      }

  private fun findAuthorsByNameContainsOrderByName(
      nameContains: String,
      asc: Boolean,
      limit: Int
  ): List<String> {
    return collection.aggregate(listOf(
        unwind(AUTHORS.toPath()),
        match(nameContains(nameContains)),
        group(AUTHORS.toPath()),
        sort(AUTHORS.order(asc)),
        limit(limit),
        project(includeId())
    ))
        .map { it.getString(ID) }
        .toList()
  }

  private fun findAuthorsByNameContainsOrderByPapers(
      nameContains: String,
      asc: Boolean,
      limit: Int
  ) = collection.aggregate(listOf(
      unwind(AUTHORS.toPath()),
      match(nameContains(nameContains)),
      group(AUTHORS.toPath(), groupCount()),
      sort(COUNT.order(asc)),
      limit(limit)
  ))
      .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  override fun findByAuthor(author: String, asc: Boolean, limit: Int, field: String,
      orderBy: String) =
      when (field) {
        PAPERS -> findPapersByAuthor(author, asc, limit, orderBy)
        KEY_PHRASES -> findArrayFieldsByAuthor(author, asc, limit, field, orderBy)
        VENUE -> findSingularFieldsByAuthor(author, asc, limit, field, orderBy)
        YEAR -> findYearsByAuthor(author, asc, limit, orderBy)
        IN_CITATIONS, OUT_CITATIONS ->
          findInOrOutCitationsByAuthor(author, asc, limit, field, orderBy)
        else -> Any()
      }

  private fun findPapersByAuthorOrderBySingularField(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.find(nameEq(author))
          .sort(field.order(asc))
          .limit(limit)
          .map { it.toPaper() }
          .toList()

  private fun findPapersByAuthorOrderByArrayField(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ): List<Paper> {
    val fieldToSize = field.toSize()
    return collection.aggregate(listOf(
        match(nameEq(author)),
        addFields(size(field.toPath(), fieldToSize)),
        sort(fieldToSize.order(asc)),
        project(exclude(fieldToSize)),
        limit(limit)
    ))
        .map { it.toPaper() }
        .toList()
  }

  private fun findPapersByAuthor(
      author: String,
      asc: Boolean,
      limit: Int,
      orderBy: String
  ) =
      if (orderBy.isArray) findPapersByAuthorOrderByArrayField(author, asc, limit, orderBy)
      else findPapersByAuthorOrderBySingularField(author, asc, limit, orderBy)

  private fun findSingularFieldsByAuthorOrderBySingularFieldsAsDocument(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ): AggregateIterable<Document> {
    return collection.aggregate(listOf(
        match(nameEq(author)),
        group(field.toPath()),
        sort(ID.order(asc)),
        limit(limit)
    ))
  }

  private fun findSingularFieldsByAuthorOrderByPapersAsDocument(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) = collection.aggregate(listOf(
      match(nameEq(author)),
      group(field.toPath(), groupCount()),
      sort(COUNT.order(asc)),
      limit(limit)
  ))

  private fun findArrayFieldsByAuthor(author: String, asc: Boolean, limit: Int, field: String,
      orderBy: String) =
      when (orderBy) {
        "papers" -> findArrayFieldsByAuthorsOrderByPapers(author, asc, limit, field)
        "keyPhrase" -> findArrayFieldsByAuthorOrderByArrayField(author, asc, limit, field)
        else -> Any()
      }

  private fun findArrayFieldsByAuthorOrderByArrayField(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(nameEq(author)),
          unwind(field.toPath()),
          group(field.toPath()),
          sort(ID.order(asc)),
          limit(limit)
      ))
          .map { it.getString(ID) }
          .toList()

  private fun findArrayFieldsByAuthorsOrderByPapers(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      collection.aggregate(listOf(
          match(nameEq(author)),
          unwind(field.toPath()),
          group(field.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  private fun findYearsByAuthor(author: String, asc: Boolean, limit: Int, orderBy: String) =
      when (orderBy) {
        "year" -> findYearsByAuthorOrderByYear(author, asc, limit)
        "papers" -> findYearsByAuthorOrdersByPapers(author, asc, limit)
        else -> Any()
      }

  private fun findYearsByAuthorOrderByYear(author: String, asc: Boolean, limit: Int) =
      findSingularFieldsByAuthorOrderBySingularFieldsAsDocument(author, asc, limit, YEAR)
          .map { it.getInteger(ID) }
          .toList()

  private fun findYearsByAuthorOrdersByPapers(author: String, asc: Boolean, limit: Int) =
      findSingularFieldsByAuthorOrderByPapersAsDocument(author, asc, limit, YEAR)
          .associateByTo(linkedMapOf(), { it.getInteger(ID) }, { it.getInteger(COUNT) })

  private fun findSingularFieldsByAuthor(author: String, asc: Boolean, limit: Int, field: String,
      orderBy: String) =
      when (orderBy) {
        "papers" -> findSingularFieldsByAuthorOrderByPapers(author, asc, limit, field)
        "venue" -> findSingularFieldsByAuthorOrderBySingularFields(author, asc, limit, field)
        else -> Any()
      }

  private fun findSingularFieldsByAuthorOrderBySingularFields(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      findSingularFieldsByAuthorOrderBySingularFieldsAsDocument(author, asc, limit, field)
          .map { it.getString(ID) }
          .toList()

  private fun findSingularFieldsByAuthorOrderByPapers(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      findSingularFieldsByAuthorOrderByPapersAsDocument(author, asc, limit, field)
          .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  private fun findInOrOutCitationsByAuthorOrderBySingularField(
      author: String,
      asc: Boolean,
      limit: Int,
      findField: String,
      field: String
  ) = collection.aggregate(listOf(
      match(nameEq(author)),
      unwind(findField.toPath()),
      group(findField.toPath()),
      lookupPaper(),
      project(documentAt0()),
      sort("$DOCUMENT.$field".order(asc)),
      limit(limit)
  ))
      .map { it.getTyped<Document>(DOCUMENT).toPaper() }
      .toList()

  private fun findInOrOutCitationsByAuthorOrderByArrayField(
      author: String,
      asc: Boolean,
      limit: Int,
      findField: String,
      field: String
  ) = collection.aggregate(listOf(
      match(nameEq(author)),
      unwind(findField.toPath()),
      group(findField.toPath()),
      lookupPaper(),
      project(documentAt0()),
      project(fields(include(DOCUMENT), size("$DOCUMENT.$field".toPath(), field.toSize()))),
      sort(field.toSize().order(asc)),
      limit(limit)
  ))
      .map { it.getTyped<Document>(DOCUMENT).toPaper() }
      .toList()

  private fun findInOrOutCitationsByAuthor(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ) =
      if (orderBy.isArray) {
        findInOrOutCitationsByAuthorOrderByArrayField(author, asc, limit, field, orderBy)
      } else {
        findInOrOutCitationsByAuthorOrderBySingularField(author, asc, limit, field, orderBy)
      }

}