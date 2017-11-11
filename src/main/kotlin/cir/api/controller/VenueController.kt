package cir.api.controller

import cir.Fields.AUTHORS
import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPERS
import cir.Fields.YEAR
import cir.api.service.VenueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class VenueController
@Autowired constructor(private val venueService: VenueService) : VenueApi {

  @GetMapping("/api/venues/{venue}/exist")
  override fun doesVenueExist(@PathVariable venue: String): Boolean {
    return venueService.doesVenueExist(venue)
  }

  @GetMapping("/api/venues")
  override fun getVenues(
      @RequestParam(required = false, defaultValue = "venue") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return venueService.findVenues(asc, limit, orderBy)
  }

  @GetMapping("/api/venues/count")
  override fun getVenuesCount(): Long {
    return venueService.countVenues()
  }

  @GetMapping("/api/venues/{venue}/papers")
  override fun getPapersByVenue(
      @PathVariable venue: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return venueService.findByVenue(venue, asc, limit, PAPERS, orderBy)
  }

  @GetMapping("/api/venues/{venue}/papers/count")
  override fun getPapersCountByVenue(@PathVariable venue: String): Long {
    return venueService.countByVenue(venue, PAPERS)
  }

  @GetMapping("/api/venues/{venue}/years")
  override fun getYearsOfPapersByVenue(
      @PathVariable venue: String,
      @RequestParam(required = false, defaultValue = "year") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return venueService.findByVenue(venue, asc, limit, YEAR, orderBy)
  }

  @GetMapping("/api/venues/{venue}/years/count")
  override fun getYearsOfPapersCountByVenue(@PathVariable venue: String): Long {
    return venueService.countByVenue(venue, YEAR)
  }

  @GetMapping("/api/venues/{venue}/keyPhrases")
  override fun getKeyPhrasesOfPapersByVenue(
      @PathVariable venue: String,
      @RequestParam(required = false, defaultValue = "keyPhrase") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return venueService.findByVenue(venue, asc, limit, KEY_PHRASES, orderBy)
  }

  @GetMapping("/api/venues/{venue}/keyPhrases/count")
  override fun getKeyPhrasesCountOfPapersByVenue(@PathVariable venue: String): Long {
    return venueService.countByVenue(venue, KEY_PHRASES)
  }

  @GetMapping("/api/venues/{venue}/authors")
  override fun getAuthorsOfPapersByVenue(
      @PathVariable venue: String,
      @RequestParam(required = false, defaultValue = "name") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return venueService.findByVenue(venue, asc, limit, AUTHORS, orderBy)
  }

  @GetMapping("/api/venues/{venue}/authors/count")
  override fun getAuthorsOfPapersCountByVenue(@PathVariable venue: String): Long {
    return venueService.countByVenue(venue, AUTHORS)
  }

  @GetMapping("/api/venues/{venue}/inCitations")
  override fun getInCitationsOfPapersByVenue(
      @PathVariable venue: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return venueService.findByVenue(venue, asc, limit, IN_CITATIONS, orderBy)
  }

  @GetMapping("/api/venues/{venue}/inCitations/count")
  override fun getInCitationsOfPapersCountByVenue(@PathVariable venue: String): Long {
    return venueService.countByVenue(venue, IN_CITATIONS)
  }

  @GetMapping("/api/venues/{venue}/outCitations")
  override fun getOutCitationsOfPapersByVenue(
      @PathVariable venue: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return venueService.findByVenue(venue, asc, limit, OUT_CITATIONS, orderBy)
  }

  @GetMapping("/api/venues/{venue}/outCitations/count")
  override fun getOutCitationsOfPapersCountByVenue(@PathVariable venue: String): Long {
    return venueService.countByVenue(venue, OUT_CITATIONS)
  }
}