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
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Service

@Service
class YearServiceImpl
@Autowired constructor(mongoOperations: MongoOperations) : YearService {

  private val collection = mongoOperations.getCollection(COLLECTION)

  private fun yearEq(year: Int) = eq(YEAR, year)

  override fun doesYearExist(year: Int): Boolean {
    return countPapersByYear(year) > 0
  }

  override fun countYears(): Long {
    return collection.aggregate(listOf(
        group(YEAR.toPath()),
        count()
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  override fun countByYear(year: Int, field: String): Long {
    return when (field) {
      PAPERS -> countPapersByYear(year)
      KEY_PHRASES, AUTHORS, VENUE, IN_CITATIONS, OUT_CITATIONS -> countFieldByYear(year, field)
      else -> 0L
    }
  }

  private fun countPapersByYear(year: Int): Long {
    return collection.count(yearEq(year))
  }

  private fun countFieldByYear(year: Int, field: String): Long {
    return if (field.isArray) {
      countArrayFieldByYear(year, field)
    } else {
      countSingularFieldByYear(year, field)
    }
  }

  private fun countArrayFieldByYear(year: Int, field: String): Long {
    return collection.aggregate(listOf(
        unwind(field.toPath()),
        match(yearEq(year)),
        group(field.toPath()),
        count()
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  private fun countSingularFieldByYear(year: Int, field: String): Long {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        group(field.toPath()),
        count()
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  override fun findYears(asc: Boolean, limit: Int, orderBy: String): Any {
    return when (orderBy) {
      "year" -> findYearsOrderByYear(asc, limit)
      "papers" -> findYearsOrderByPapers(asc, limit)
      else -> Any()
    }
  }

  private fun findYearsOrderByYear(asc: Boolean, limit: Int): List<Int> {
    return collection.aggregate(listOf(
        group(YEAR.toPath()),
        sort(ID.order(asc)),
        limit(limit),
        project(includeId())
    ))
        .map { it.getInteger(ID) }
        .toList()
  }

  private fun findYearsOrderByPapers(asc: Boolean, limit: Int): LinkedHashMap<Int, Int> {
    return collection.aggregate(listOf(
        group(YEAR.toPath(), groupCount()),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getInteger(ID) }, { it.getInteger(COUNT) })
  }

  override fun findByYear(year: Int, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when (field) {
      PAPERS -> findPapersByYear(year, asc, limit, orderBy)
      AUTHORS, KEY_PHRASES -> findArrayFieldsByYear(year, asc, limit, field, orderBy)
      VENUE -> findSingularFieldsByYear(year, asc, limit, field, orderBy)
      IN_CITATIONS, OUT_CITATIONS ->
        findCitationsByYear(year, asc, limit, field, orderBy)
      else -> Any()
    }
  }

  private fun findPapersByYear(year: Int, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findPapersByYearOrderByArrayField(year, asc, limit, orderBy)
    } else {
      findPapersByYearOrderBySingularField(year, asc, limit, orderBy)
    }
  }

  private fun findPapersByYearOrderByArrayField(year: Int, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        addFields(size(orderBy.toField().toPath(), orderBy.toField().toSize())),
        sort(orderBy.toField().toSize().order(asc)),
        project(Projections.exclude(orderBy.toField().toSize())),
        limit(limit)
    ))
        .map { it.toPaper() }
        .toList()
  }

  private fun findPapersByYearOrderBySingularField(year: Int, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return collection.find(yearEq(year))
        .sort(orderBy.toField().order(asc))
        .limit(limit)
        .map { it.toPaper() }
        .toList()
  }

  private fun findArrayFieldsByYear(year: Int, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when (orderBy.toField()) {
      PAPERS -> findArrayFieldsByYearOrderByPapers(year, asc, limit, field)
      AUTHORS, KEY_PHRASES -> findArrayFieldsByYearOrderByArrayField(year, asc, limit, field)
      else -> Any()
    }
  }

  private fun findArrayFieldsByYearOrderByPapers(year: Int, asc: Boolean, limit: Int,
      field: String): LinkedHashMap<String, Int> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        unwind(field.toPath()),
        group(field.toPath(), groupCount()),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })
  }

  private fun findArrayFieldsByYearOrderByArrayField(year: Int, asc: Boolean, limit: Int,
      field: String): List<String> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        unwind(field.toPath()),
        group(field.toPath()),
        sort(ID.order(asc)),
        limit(limit)
    ))
        .map { it.getString(ID) }
        .toList()
  }

  private fun findSingularFieldsByYear(year: Int, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when (orderBy) {
      "papers" -> findSingularFieldsByYearOrderByPapers(year, asc, limit, field)
      "venue" -> findSingularFieldsByYearOrderBySingularField(year, asc, limit, field)
      else -> Any()
    }
  }

  private fun findSingularFieldsByYearOrderByPapers(year: Int, asc: Boolean, limit: Int,
      field: String): LinkedHashMap<Any, Int> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        group(field.toPath(), groupCount()),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getValue(ID) }, { it.getInteger(COUNT) })
  }

  private fun findSingularFieldsByYearOrderBySingularField(year: Int, asc: Boolean, limit: Int,
      field: String): List<Any> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        group(field.toPath()),
        sort(ID.order(asc)),
        limit(limit)
    ))
        .map { it.getValue(ID) }
        .toList()
  }

  private fun findCitationsByYear(year: Int, asc: Boolean, limit: Int, field: String,
      orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findCitationsByYearOrderByArrayField(year, asc, limit, field, orderBy)
    } else {
      findCitationsByYearOrderBySingularField(year, asc, limit, field, orderBy)
    }
  }

  private fun findCitationsByYearOrderByArrayField(year: Int, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        unwind(field.toPath()),
        group(field.toPath()),
        lookupPaper(),
        project(documentAt0()),
        project(Projections.fields(Projections.include(DOCUMENT),
            size("$DOCUMENT.${orderBy.toField()}".toPath(), orderBy.toField().toSize()))),
        sort(orderBy.toField().toSize().order(asc)),
        limit(limit)
    ))
        .map { it.getTyped<Document>(DOCUMENT).toPaper() }
        .toList()
  }

  private fun findCitationsByYearOrderBySingularField(year: Int, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(yearEq(year)),
        unwind(field.toPath()),
        group(field.toPath()),
        lookupPaper(),
        project(documentAt0()),
        sort("$DOCUMENT.${orderBy.toField()}".order(asc)),
        limit(limit)
    ))
        .map { it.getTyped<Document>(DOCUMENT).toPaper() }
        .toList()
  }
}