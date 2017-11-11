package cir.api.service

interface VenueService {

  fun doesVenueExist(venue: String): Boolean

  fun countVenues(): Long

  fun countByVenue(venue: String, field: String): Long

  fun findVenues(asc: Boolean, limit: Int, orderBy: String): Any

  fun findByVenue(venue: String, asc: Boolean, limit: Int, field: String, orderBy: String): Any
}