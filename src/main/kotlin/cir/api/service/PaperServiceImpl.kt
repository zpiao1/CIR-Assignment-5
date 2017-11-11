package cir.api.service

import cir.*
import cir.Fields.COUNT
import cir.Fields.DOCUMENT
import cir.Fields.ID
import cir.Fields.IN_CITATIONS
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPERS
import cir.Fields.TITLE
import cir.Fields.YEAR
import cir.data.entity.Paper
import cir.data.entity.getTyped
import cir.data.entity.toPaper
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.regex
import com.mongodb.client.model.Projections.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Service
import java.util.regex.Pattern.CASE_INSENSITIVE

@Service
class PaperServiceImpl
@Autowired constructor(mongoOperations: MongoOperations) : PaperService {

  private val collection = mongoOperations.getCollection(COLLECTION)

  private fun idEq(id: String) = eq(ID, id)

  private fun titleContains(titleContains: String) =
      regex(TITLE, ".*$titleContains.*".toPattern(CASE_INSENSITIVE))

  override fun doesPaperExist(id: String): Boolean {
    return collection.count(idEq(id)) > 0
  }

  override fun countPapers(): Long {
    return collection.count()
  }

  override fun countPapersByTitleContains(titleContains: String): Long {
    return collection.count(titleContains(titleContains))
  }

  override fun countByPaper(id: String, field: String): Long {
    return collection.aggregate(listOf(
        match(idEq(id)),
        project(fields(size(field.toPath(), COUNT), excludeId()))
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  override fun findPapers(asc: Boolean, limit: Int, orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findPapersOrderByArrayField(asc, limit, orderBy)
    } else {
      findPapersOrderBySingularField(asc, limit, orderBy)
    }
  }

  private fun findPapersOrderByArrayField(asc: Boolean, limit: Int, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        addFields(size(orderBy.toField().toPath(), orderBy.toField().toSize())),
        sort(orderBy.toField().toSize().order(asc)),
        limit(limit),
        project(exclude(orderBy.toField().toSize()))
    ))
        .map { it.toPaper() }
        .toList()
  }

  private fun findPapersOrderBySingularField(asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return collection.find()
        .sort(orderBy.toField().order(asc))
        .limit(limit)
        .map { it.toPaper() }
        .toList()
  }

  override fun findPapersByTitleContains(titleContains: String, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findPapersByTitleContainsOrderByArrayField(titleContains, asc, limit, orderBy)
    } else {
      findPapersByTitleContainsOrderBySingularField(titleContains, asc, limit, orderBy)
    }
  }

  private fun findPapersByTitleContainsOrderByArrayField(titleContains: String, asc: Boolean,
      limit: Int, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(titleContains(titleContains)),
        addFields(size(orderBy.toField().toPath(), orderBy.toField().toSize())),
        sort(orderBy.toField().toSize().order(asc)),
        limit(limit),
        project(exclude(orderBy.toField().toSize()))
    ))
        .map { it.toPaper() }
        .toList()
  }

  private fun findPapersByTitleContainsOrderBySingularField(titleContains: String, asc: Boolean,
      limit: Int, orderBy: String): List<Paper> {
    return collection.find(titleContains(titleContains))
        .sort(orderBy.toField().order(asc))
        .limit(limit)
        .map { it.toPaper() }
        .toList()
  }

  override fun findPaperById(id: String): Paper? {
    return collection.find(idEq(id)).first()?.toPaper()
  }

  override fun findByPaper(id: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when {
      field == YEAR -> findYearByPaper(id)
      field == IN_CITATIONS || field == OUT_CITATIONS -> findCitationsByPaper(id, asc, limit, field,
          orderBy)
      field.isArray -> findArrayFieldsByPaper(id, asc, limit, field, orderBy)
      !field.isArray -> findSingularFieldByPaper(id, field)
      else -> Any()
    }
  }

  private fun findYearByPaper(id: String): Int {
    return collection.find(idEq(id))
        .projection(fields(include(YEAR), excludeId()))
        .first()
        .getInteger(YEAR)
  }

  private fun findCitationsByPaper(id: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findCitationsByPaperOrderByArrayField(id, asc, limit, field, orderBy)
    } else {
      findCitationsByPaperOrderBySingularField(id, asc, limit, field, orderBy)
    }
  }

  private fun findCitationsByPaperOrderByArrayField(id: String, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(idEq(id)),
        unwind(field.toPath()),
        group(field.toPath()),
        lookupPaper(),
        project(documentAt0()),
        project(fields(include(DOCUMENT),
            size("$DOCUMENT.${orderBy.toField()}".toPath(),
                orderBy.toField().toSize()))),
        sort(orderBy.toField().toSize().order(asc)),
        project(excludeId()),
        limit(limit)
    ))
        .map { it.getTyped<Document>(DOCUMENT).toPaper() }
        .toList()
  }

  private fun findCitationsByPaperOrderBySingularField(id: String, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(idEq(id)),
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

  private fun findArrayFieldsByPaper(id: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return if (orderBy == "papers") {
      findArrayFieldsByPaperOrderByPapers(id, asc, limit, field)
    } else {
      findArrayFieldsByPaperOrderByArrayFields(id, asc, limit, field, orderBy)
    }
  }

  private fun findArrayFieldsByPaperOrderByPapers(id: String, asc: Boolean, limit: Int,
      field: String): LinkedHashMap<String, Int> {
    return collection.aggregate(listOf(
        match(idEq(id)),
        unwind(field.toPath()),
        group(field.toPath()),
        lookup(COLLECTION, ID, field, PAPERS),
        project(size(PAPERS.toPath(), COUNT)),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })
  }

  private fun findArrayFieldsByPaperOrderByArrayFields(id: String, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<String> {
    return collection.aggregate(listOf(
        match(idEq(id)),
        unwind(field.toPath()),
        group(field.toPath()),
        sort(ID.order(asc)),
        limit(limit)
    ))
        .map { it.getString(ID) }
        .toList()
  }

  private fun findSingularFieldByPaper(id: String, field: String): String {
    return collection.find(idEq(id))
        .projection(fields(include(field), excludeId()))
        .first()
        .getString(field)
  }
}