package cir.api.controller

interface VenueApi {

  fun doesVenueExist(venue: String): Boolean

  fun getVenues(
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getVenuesCount(): Long

  fun getPapersByVenue(
      venue: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getPapersCountByVenue(venue: String): Long

  fun getYearsOfPapersByVenue(
      venue: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getYearsOfPapersCountByVenue(venue: String): Long

  fun getKeyPhrasesOfPapersByVenue(
      venue: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getKeyPhrasesCountOfPapersByVenue(venue: String): Long

  fun getAuthorsOfPapersByVenue(
      venue: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getAuthorsOfPapersCountByVenue(venue: String): Long

  fun getInCitationsOfPapersByVenue(
      venue: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getInCitationsOfPapersCountByVenue(venue: String): Long

  fun getOutCitationsOfPapersByVenue(
      venue: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getOutCitationsOfPapersCountByVenue(venue: String): Long
}