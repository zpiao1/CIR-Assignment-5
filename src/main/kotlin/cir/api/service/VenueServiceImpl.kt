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
import com.mongodb.client.model.Projections.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Service

@Service
class VenueServiceImpl
@Autowired constructor(mongoOperations: MongoOperations) : VenueService {

  private val collection = mongoOperations.getCollection(COLLECTION)

  private fun venueEq(venue: String) = eq(VENUE, venue)

  override fun doesVenueExist(venue: String): Boolean {
    return countPapersByVenue(venue) > 0
  }

  override fun countVenues(): Long {
    return collection.aggregate(listOf(
        group(VENUE.toPath()),
        count()
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  override fun countByVenue(venue: String, field: String): Long {
    return when (field) {
      PAPERS -> countPapersByVenue(venue)
      KEY_PHRASES, AUTHORS, YEAR, IN_CITATIONS, OUT_CITATIONS -> countFieldByVenue(venue, field)
      else -> 0L
    }
  }

  private fun countPapersByVenue(venue: String): Long {
    return collection.count(venueEq(venue))
  }

  private fun countFieldByVenue(venue: String, field: String): Long {
    return if (field.isArray) {
      countArrayFieldByVenue(venue, field)
    } else {
      countSingularFieldByVenue(venue, field)
    }
  }

  private fun countArrayFieldByVenue(venue: String, field: String): Long {
    return collection.aggregate(listOf(
        unwind(field.toPath()),
        match(venueEq(venue)),
        group(field.toPath()),
        count()
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  private fun countSingularFieldByVenue(venue: String, field: String): Long {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        group(field.toPath()),
        count()
    ))
        .first()
        .getInteger(COUNT)
        .toLong()
  }

  override fun findVenues(asc: Boolean, limit: Int, orderBy: String): Any {
    return when (orderBy) {
      "venue" -> findVenuesOrderByVenue(asc, limit)
      "papers" -> findVenuesOrderByPapers(asc, limit)
      else -> Any()
    }
  }

  private fun findVenuesOrderByVenue(asc: Boolean, limit: Int): List<String> {
    return collection.aggregate(listOf(
        group(VENUE.toPath()),
        sort(VENUE.order(asc)),
        limit(limit),
        project(includeId())
    ))
        .map { it.getString(ID) }
        .toList()
  }

  private fun findVenuesOrderByPapers(asc: Boolean, limit: Int): LinkedHashMap<String, Int> {
    return collection.aggregate(listOf(
        group(VENUE.toPath(), groupCount()),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })
  }

  override fun findByVenue(venue: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when (field) {
      PAPERS -> findPapersByVenue(venue, asc, limit, orderBy)
      AUTHORS, KEY_PHRASES -> findArrayFieldsByVenue(venue, asc, limit, field, orderBy)
      YEAR -> findSingularFieldsByVenue(venue, asc, limit, field, orderBy)
      IN_CITATIONS, OUT_CITATIONS ->
        findCitationsByVenue(venue, asc, limit, field, orderBy)
      else -> Any()
    }
  }

  private fun findPapersByVenue(venue: String, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findPapersByVenueOrderByArrayField(venue, asc, limit, orderBy)
    } else {
      findPapersByVenueOrderBySingularField(venue, asc, limit, orderBy)
    }
  }

  private fun findPapersByVenueOrderByArrayField(venue: String, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        addFields(size(orderBy.toField().toPath(), orderBy.toField().toSize())),
        sort(orderBy.toField().toSize().order(asc)),
        project(exclude(orderBy.toField().toSize())),
        limit(limit)
    ))
        .map { it.toPaper() }
        .toList()
  }

  private fun findPapersByVenueOrderBySingularField(venue: String, asc: Boolean, limit: Int,
      orderBy: String): List<Paper> {
    return collection.find(venueEq(venue))
        .sort(orderBy.toField().order(asc))
        .limit(limit)
        .map { it.toPaper() }
        .toList()
  }

  private fun findArrayFieldsByVenue(venue: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when (orderBy.toField()) {
      PAPERS -> findArrayFieldsByVenueOrderByPapers(venue, asc, limit, field)
      AUTHORS, KEY_PHRASES -> findArrayFieldsByVenueOrderByArrayField(venue, asc, limit, field)
      else -> Any()
    }
  }

  private fun findArrayFieldsByVenueOrderByPapers(venue: String, asc: Boolean, limit: Int,
      field: String): LinkedHashMap<String, Int> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        unwind(field.toPath()),
        group(field.toPath(), groupCount()),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getString(ID) }, { it.getInteger(COUNT) })
  }

  private fun findArrayFieldsByVenueOrderByArrayField(venue: String, asc: Boolean, limit: Int,
      field: String): List<String> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        unwind(field.toPath()),
        group(field.toPath()),
        sort(ID.order(asc)),
        limit(limit)
    ))
        .map { it.getString(ID) }
        .toList()
  }

  private fun findSingularFieldsByVenue(venue: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): Any {
    return when (orderBy) {
      "papers" -> findSingularFieldsByVenueOrderByPapers(venue, asc, limit, field)
      "year" -> findSingularFieldsByVenueOrderBySingularField(venue, asc, limit, field)
      else -> Any()
    }
  }

  private fun findSingularFieldsByVenueOrderByPapers(venue: String, asc: Boolean, limit: Int,
      field: String): LinkedHashMap<Any, Int> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        group(field.toPath(), groupCount()),
        sort(COUNT.order(asc)),
        limit(limit)
    ))
        .associateByTo(linkedMapOf(), { it.getValue(ID) }, { it.getInteger(COUNT) })
  }

  private fun findSingularFieldsByVenueOrderBySingularField(venue: String, asc: Boolean, limit: Int,
      field: String): List<Any> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        group(field.toPath()),
        sort(ID.order(asc)),
        limit(limit)
    ))
        .map { it.getValue(ID) }
        .toList()
  }

  private fun findCitationsByVenue(venue: String, asc: Boolean, limit: Int, field: String,
      orderBy: String): List<Paper> {
    return if (orderBy.toField().isArray) {
      findCitationsByVenueOrderByArrayField(venue, asc, limit, field, orderBy)
    } else {
      findCitationsByVenueOrderBySingularField(venue, asc, limit, field, orderBy)
    }
  }

  private fun findCitationsByVenueOrderByArrayField(venue: String, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
        unwind(field.toPath()),
        group(field.toPath()),
        lookupPaper(),
        project(documentAt0()),
        project(fields(include(DOCUMENT),
            size("$DOCUMENT.${orderBy.toField()}".toPath(), orderBy.toField().toSize()))),
        sort(orderBy.toField().toSize().order(asc)),
        limit(limit)
    ))
        .map { it.getTyped<Document>(DOCUMENT).toPaper() }
        .toList()
  }

  private fun findCitationsByVenueOrderBySingularField(venue: String, asc: Boolean, limit: Int,
      field: String, orderBy: String): List<Paper> {
    return collection.aggregate(listOf(
        match(venueEq(venue)),
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