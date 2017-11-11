package cir.api.controller

import cir.Fields.AUTHORS
import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPERS
import cir.Fields.VENUE
import cir.api.service.YearService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class YearController
@Autowired constructor(private val yearService: YearService) : YearApi {

  @GetMapping("/api/years/{year}/exist")
  override fun doesYearExist(@PathVariable year: Int): Boolean {
    return yearService.doesYearExist(year)
  }

  @GetMapping("/api/years")
  override fun getYears(
      @RequestParam(required = false, defaultValue = "year") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return yearService.findYears(asc, limit, orderBy)
  }

  @GetMapping("/api/years/count")
  override fun getYearsCount(): Long {
    return yearService.countYears()
  }

  @GetMapping("/api/years/{year}/papers")
  override fun getPapersByYear(
      @PathVariable year: Int,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return yearService.findByYear(year, asc, limit, PAPERS, orderBy)
  }

  @GetMapping("/api/years/{year}/papers/count")
  override fun getPapersCountByYear(@PathVariable year: Int): Long {
    return yearService.countByYear(year, PAPERS)
  }

  @GetMapping("/api/years/{year}/venues")
  override fun getVenuesOfPapersByYear(
      @PathVariable year: Int,
      @RequestParam(required = false, defaultValue = "venue") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return yearService.findByYear(year, asc, limit, VENUE, orderBy)
  }

  @GetMapping("/api/years/{year}/venues/count")
  override fun getVenuesOfPapersCountByYear(@PathVariable year: Int): Long {
    return yearService.countByYear(year, VENUE)
  }

  @GetMapping("/api/years/{year}/keyPhrases")
  override fun getKeyPhrasesOfPapersByYear(
      @PathVariable year: Int,
      @RequestParam(required = false, defaultValue = "keyPhrase") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return yearService.findByYear(year, asc, limit, KEY_PHRASES, orderBy)
  }

  @GetMapping("/api/years/{year}/keyPhrases/count")
  override fun getKeyPhrasesCountOfPapersByYear(@PathVariable year: Int): Long {
    return yearService.countByYear(year, KEY_PHRASES)
  }

  @GetMapping("/api/years/{year}/authors")
  override fun getAuthorsOfPapersByYear(
      @PathVariable year: Int,
      @RequestParam(required = false, defaultValue = "name") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return yearService.findByYear(year, asc, limit, AUTHORS, orderBy)
  }

  @GetMapping("/api/years/{year}/authors/count")
  override fun getAuthorsOfPapersCountByYear(@PathVariable year: Int): Long {
    return yearService.countByYear(year, AUTHORS)
  }

  @GetMapping("/api/years/{year}/inCitations")
  override fun getInCitationsOfPapersByYear(
      @PathVariable year: Int,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return yearService.findByYear(year, asc, limit, IN_CITATIONS, orderBy)
  }

  @GetMapping("/api/years/{year}/inCitations/count")
  override fun getInCitationsOfPapersCountByYear(@PathVariable year: Int): Long {
    return yearService.countByYear(year, IN_CITATIONS)
  }

  @GetMapping("/api/years/{year}/outCitations")
  override fun getOutCitationsOfPapersByYear(
      @PathVariable year: Int,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return yearService.findByYear(year, asc, limit, OUT_CITATIONS, orderBy)
  }

  @GetMapping("/api/years/{year}/outCitations/count")
  override fun getOutCitationsOfPapersCountByYear(@PathVariable year: Int): Long {
    return yearService.countByYear(year, OUT_CITATIONS)
  }
}