package cir.api.controller

import cir.Fields.AUTHORS
import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.VENUE
import cir.Fields.YEAR
import cir.api.NotFoundException
import cir.api.service.PaperService
import cir.data.entity.Paper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PaperController
@Autowired constructor(private val paperService: PaperService) : PaperApi {

  @GetMapping("/api/papers/{id}/exist")
  override fun doesPaperExist(@PathVariable id: String): Boolean {
    return paperService.doesPaperExist(id)
  }

  @GetMapping("/api/papers")
  override fun getPapers(
      @RequestParam(required = false) titleContains: String?,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): List<Paper> {
    return if (titleContains == null || titleContains.isEmpty()) {
      paperService.findPapers(asc, limit, orderBy)
    } else {
      paperService.findPapersByTitleContains(titleContains, asc, limit, orderBy)
    }
  }

  @GetMapping("/api/papers/count")
  override fun getPapersCount(): Long {
    return paperService.countPapers()
  }

  @GetMapping("/api/papers/{id}")
  override fun getPaperById(@PathVariable id: String): Paper {
    return paperService.findPaperById(id)
        ?: throw NotFoundException("Cannot find paper with Id: $id")
  }

  @GetMapping("/api/papers/{id}/authors")
  override fun getAuthorsOfPaper(
      @PathVariable id: String,
      @RequestParam(required = false, defaultValue = "name") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return paperService.findByPaper(id, asc, limit, AUTHORS, orderBy)
  }

  @GetMapping("/api/papers/{id}/authors/count")
  override fun getAuthorsCountOfPaper(@PathVariable id: String): Long {
    return paperService.countByPaper(id, AUTHORS)
  }

  @GetMapping("/api/papers/{id}/year")
  override fun getYearOfPaper(@PathVariable id: String): Any {
    return paperService.findByPaper(id, false, 1, YEAR, "year")
  }

  @GetMapping("/api/papers/{id}/keyPhrases")
  override fun getKeyPhrasesOfPaper(
      @PathVariable id: String,
      @RequestParam(required = false, defaultValue = "keyPhrase") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int
  ): Any {
    return paperService.findByPaper(id, asc, limit, KEY_PHRASES, orderBy)
  }

  @GetMapping("/api/papers/{id}/keyPhrases/count")
  override fun getKeyPhrasesCountOfPaper(@PathVariable id: String): Long {
    return paperService.countByPaper(id, KEY_PHRASES)
  }

  @GetMapping("/api/papers/{id}/venue")
  override fun getVenueOfPaper(@PathVariable id: String): Any {
    return paperService.findByPaper(id, false, 1, VENUE, "venue")
  }

  @GetMapping("/api/papers/{id}/inCitations")
  override fun getInCitationsOfPaper(
      @PathVariable id: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return paperService.findByPaper(id, asc, limit, IN_CITATIONS, orderBy)
  }

  @GetMapping("/api/papers/{id}/inCitations/count")
  override fun getInCitationsCountOfPaper(@PathVariable id: String): Long {
    return paperService.countByPaper(id, IN_CITATIONS)
  }

  @GetMapping("/api/papers/{id}/outCitations")
  override fun getOutCitationsOfPaper(
      @PathVariable id: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int
  ): Any {
    return paperService.findByPaper(id, asc, limit, OUT_CITATIONS, orderBy)
  }

  @GetMapping("/api/papers/{id}/outCitations/count")
  override fun getOutCitationsCountOfPaper(@PathVariable id: String): Long {
    return paperService.countByPaper(id, OUT_CITATIONS)
  }
}