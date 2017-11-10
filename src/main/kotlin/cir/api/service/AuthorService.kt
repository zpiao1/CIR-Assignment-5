package cir.api.service

import cir.*
import cir.Fields.AUTHORS
import cir.Fields.COUNT
import cir.Fields.DOCUMENT
import cir.Fields.ID
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
class AuthorService
@Autowired constructor(mongoOperations: MongoOperations) {

  private val collection = mongoOperations.getCollection("paper")

  private fun nameContains(nameContains: String) =
      regex(AUTHORS, Pattern.compile(".*$nameContains.*", CASE_INSENSITIVE))

  private fun nameEq(name: String) = eq(AUTHORS, name)

  /**
   * Count the number of papers containing this author
   */
  fun doesAuthorExist(author: String) = countPapersByAuthor(author) > 0

  fun countAuthors(): Long =
      collection.aggregate(listOf(
          unwind(AUTHORS.toPath()),
          group(AUTHORS.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()


  fun countAuthorsByNameContains(nameContains: String) =
      collection.aggregate(listOf(
          unwind(AUTHORS.toPath()),
          match(nameContains(nameContains)),
          group(AUTHORS.toPath()),
          count()
      ))
          .first()
          .getInteger(COUNT)
          .toLong()

  fun countPapersByAuthor(author: String) = collection.count(Document("authors", author))

  fun countFieldByAuthor(author: String, field: String) =
      if (field.isArray) countWithUnwinding(author, field)
      else countWithoutUnwinding(author, field)

  private fun countAggregates(author: String, field: String) =
      listOf(
          match(Document(AUTHORS, author)),
          group(field.toPath()),
          count()
      )

  private fun countWithUnwinding(author: String, field: String): Long {
    val list = mutableListOf(unwind(field.toPath()))
    list.addAll(countAggregates(author, field))
    return collection.aggregate(list)
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  private fun countWithoutUnwinding(author: String, field: String): Long =
      collection.aggregate(countAggregates(author, field))
          .first()
          .getInteger(COUNT)
          .toLong()

  fun findAuthorsOrderByName(asc: Boolean, limit: Int): List<String> {
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

  fun findAuthorsOrderByPapers(asc: Boolean, limit: Int) =
      collection.aggregate(listOf(
          unwind(AUTHORS.toPath()),
          group(AUTHORS.toPath(), groupCount()),
          sort(COUNT.order(asc)),
          limit(limit)
      ))
          .associateByTo(hashMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  fun findAuthorsByNameContainsOrderByName(
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

  fun findAuthorsByNameContainsOrderByPapers(
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
      .associateByTo(hashMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

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

  fun findPapersByAuthor(
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

  private fun findSingularFieldByAuthorOrderByPapersAsDocument(
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

  fun findArrayFieldsByAuthorOrderByArrayField(
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

  fun findArrayFieldByAuthorsOrderByPapers(
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
          .associateByTo(hashMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

  fun findYearsByAuthorOrderByYears(author: String, asc: Boolean, limit: Int) =
      findSingularFieldsByAuthorOrderBySingularFieldsAsDocument(author, asc, limit, YEAR)
          .map { it.getInteger(ID) }
          .toList()

  fun findYearsByAuthorOrdersByPapers(author: String, asc: Boolean, limit: Int) =
      findSingularFieldByAuthorOrderByPapersAsDocument(author, asc, limit, YEAR)
          .associateByTo(hashMapOf(), { it.getInteger(ID) }, { it.getInteger(COUNT) })

  fun findSingularFieldsByAuthorOrderBySingularFields(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      findSingularFieldsByAuthorOrderBySingularFieldsAsDocument(author, asc, limit, field)
          .map { it.getString(ID) }
          .toList()

  fun findSingularFieldsByAuthorOrderByPapers(
      author: String,
      asc: Boolean,
      limit: Int,
      field: String
  ) =
      findSingularFieldByAuthorOrderByPapersAsDocument(author, asc, limit, field)
          .associateByTo(hashMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })

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

  fun findInOrOutCitationsByAuthor(
      author: String,
      asc: Boolean,
      limit: Int,
      findField: String,
      field: String
  ) =
      if (field.isArray) {
        findInOrOutCitationsByAuthorOrderByArrayField(author, asc, limit, findField, field)
      } else {
        findInOrOutCitationsByAuthorOrderBySingularField(author, asc, limit, findField, field)
      }

}