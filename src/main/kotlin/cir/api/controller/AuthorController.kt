package cir.api.controller

import cir.Fields.IN_CITATIONS
import cir.Fields.KEY_PHRASES
import cir.Fields.OUT_CITATIONS
import cir.Fields.VENUE
import cir.Fields.YEAR
import cir.api.service.AuthorService
import cir.toField
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorController
@Autowired constructor(private val authorService: AuthorService) : AuthorApi {

  @GetMapping("/api/authors/{name}/exist")
  override fun doesAuthorExist(@PathVariable(required = true) name: String) =
      authorService.doesAuthorExist(name)

  @GetMapping("/api/authors")
  override fun getAuthors(
      @RequestParam(required = false) nameContains: String?,
      @RequestParam(required = false, defaultValue = "name") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      when (orderBy) {
        "name" -> when (nameContains) {
          null, "" -> authorService.findAuthorsOrderByName(asc, limit)
          else -> authorService.findAuthorsByNameContainsOrderByName(nameContains, asc, limit)
        }
        "papers" -> when (nameContains) {
          null, "" -> authorService.findAuthorsOrderByPapers(asc, limit)
          else -> authorService.findAuthorsByNameContainsOrderByPapers(nameContains, asc, limit)
        }
        else -> Any()
      }

  @GetMapping("/api/authors/count")
  override fun getAuthorsCount() = authorService.countAuthors()

  @GetMapping("/api/authors/{name}/papers")
  override fun getPapersByAuthor(
      @PathVariable(required = true) name: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int) =
      authorService.findPapersByAuthor(name, asc, limit, orderBy.toField())

  @GetMapping("/api/authors/{name}/papers/count")
  override fun getPapersCountByAuthor(@PathVariable(required = true) name: String) =
      authorService.countPapersByAuthor(name)

  @GetMapping("/api/authors/{name}/years")
  override fun getYearsOfPapersByAuthor(
      @PathVariable(required = true) name: String,
      @RequestParam(required = false, defaultValue = "year") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      when (orderBy) {
        "year" -> authorService.findYearsByAuthorOrderByYears(name, asc, limit)
        "papers" -> authorService.findYearsByAuthorOrdersByPapers(name, asc, limit)
        else -> Any()
      }

  @GetMapping("/api/authors/{name}/years/count")
  override fun getYearsOfPapersCountByAuthor(@PathVariable(required = true) name: String) =
      authorService.countFieldByAuthor(name, YEAR)

  @GetMapping("/api/authors/{name}/keyPhrases")
  override fun getKeyPhrasesOfPapersByAuthor(
      @PathVariable(required = true) name: String,
      @RequestParam(required = false, defaultValue = "keyPhrase") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      when (orderBy) {
        "keyPhrase" ->
          authorService.findArrayFieldsByAuthorOrderByArrayField(name, asc, limit, KEY_PHRASES)
        "papers" ->
          authorService.findArrayFieldByAuthorsOrderByPapers(name, asc, limit, KEY_PHRASES)
        else -> Any()
      }

  @GetMapping("/api/authors/{name}/keyPhrases/count")
  override fun getKeyPhrasesCountOfPapersByAuthor(@PathVariable(required = true) name: String) =
      authorService.countFieldByAuthor(name, KEY_PHRASES)

  @GetMapping("/api/authors/{name}/venues")
  override fun getVenuesOfPapersByAuthor(
      @PathVariable(required = true) name: String,
      @RequestParam(required = false, defaultValue = "venue") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      when (orderBy) {
        "venue" ->
          authorService.findSingularFieldsByAuthorOrderBySingularFields(name, asc, limit, VENUE)
        "papers" -> authorService.findSingularFieldsByAuthorOrderByPapers(name, asc, limit, VENUE)
        else -> Any()
      }

  @GetMapping("/api/authors/{name}/venues/count")
  override fun getVenuesCountOfPapersByAuthor(@PathVariable(required = true) name: String) =
      authorService.countFieldByAuthor(name, VENUE)

  @GetMapping("/api/authors/{name}/inCitations")
  override fun getInCitationsOfPapersByAuthor(
      @PathVariable(required = true) name: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int) =
      authorService.findInOrOutCitationsByAuthor(name, asc, limit, IN_CITATIONS, orderBy.toField())

  @GetMapping("/api/authors/{name}/inCitations/count")
  override fun getInCitationsCountOfPapersByAuthor(@PathVariable(required = true) name: String) =
      authorService.countFieldByAuthor(name, IN_CITATIONS)

  @GetMapping("/api/authors/{name}/outCitations")
  override fun getOutCitationsOfPapersByAuthor(
      @PathVariable(required = true) name: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int) =
      authorService.findInOrOutCitationsByAuthor(name, asc, limit, OUT_CITATIONS, orderBy.toField())

  @GetMapping("/api/authors/{name}/outCitations/count")
  override fun getOutCitationsCountOfPapersByAuthor(@PathVariable(required = true) name: String) =
      authorService.countFieldByAuthor(name, OUT_CITATIONS)
}