package cir.api.controller

import cir.Fields.AUTHORS
import cir.Fields.IN_CITATIONS
import cir.Fields.OUT_CITATIONS
import cir.Fields.PAPERS
import cir.Fields.VENUE
import cir.Fields.YEAR
import cir.api.service.KeyPhraseService
import cir.toField
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class KeyPhrasesController
@Autowired constructor(private val keyPhraseService: KeyPhraseService) : KeyPhrasesApi {

  @GetMapping("/api/keyPhrases/{keyPhrase}/exist")
  override fun doesKeyPhraseExist(@PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.doesKeyPhraseExist(keyPhrase)

  @GetMapping("/api/keyPhrases")
  override fun getKeyPhrases(
      @RequestParam(required = false, defaultValue = "name") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      keyPhraseService.findKeyPhrases(asc, limit, orderBy)

  @GetMapping("/api/keyPhrases/count")
  override fun getKeyPhrasesCount() = keyPhraseService.countKeyPhrases()

  @GetMapping("/api/keyPhrases/{keyPhrase}/papers")
  override fun getPapersByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int) =
      keyPhraseService.findByKeyPhrase(keyPhrase, asc, limit, PAPERS, orderBy.toField())

  @GetMapping("/api/keyPhrases/{keyPhrase}/papers/count")
  override fun getPapersCountByKeyPhrase(@PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.countByKeyPhrase(keyPhrase, PAPERS)

  @GetMapping("/api/keyPhrases/{keyPhrase}/years")
  override fun getYearsOfPapersByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String,
      @RequestParam(required = false, defaultValue = "year") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      keyPhraseService.findByKeyPhrase(keyPhrase, asc, limit, YEAR, orderBy)

  @GetMapping("/api/keyPhrases/{keyPhrase}/years/count")
  override fun getYearsOfPapersCountByKeyPhrase(@PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.countByKeyPhrase(keyPhrase, YEAR)

  @GetMapping("/api/keyPhrases/{keyPhrase}/venues")
  override fun getVenuesOfPapersByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String,
      @RequestParam(required = false, defaultValue = "venue") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      keyPhraseService.findByKeyPhrase(keyPhrase, asc, limit, VENUE, orderBy)

  @GetMapping("/api/keyPhrases/{keyPhrase}/venues/count")
  override fun getVenuesOfPapersCountByKeyPhrase(@PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.countByKeyPhrase(keyPhrase, VENUE)

  @GetMapping("/api/keyPhrases/{keyPhrase}/authors")
  override fun getAuthorsOfPapersByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String,
      @RequestParam(required = false, defaultValue = "name") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "50") limit: Int) =
      keyPhraseService.findByKeyPhrase(keyPhrase, asc, limit, AUTHORS, orderBy)

  @GetMapping("/api/keyPhrases/{keyPhrase}/authors/count")
  override fun getAuthorsOfPapersCountByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.countByKeyPhrase(keyPhrase, AUTHORS)

  @GetMapping("/api/keyPhrases/{keyPhrase}/inCitations")
  override fun getInCitationsOfPapersByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int) =
      keyPhraseService.findByKeyPhrase(keyPhrase, asc, limit, IN_CITATIONS, orderBy.toField())

  @GetMapping("/api/keyPhrases/{keyPhrase}/inCitations/count")
  override fun getInCitationsOfPapersCountByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.countByKeyPhrase(keyPhrase, IN_CITATIONS)

  @GetMapping("/api/keyPhrases/{keyPhrase}/outCitations")
  override fun getOutCitationsOfPapersByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String,
      @RequestParam(required = false, defaultValue = "title") orderBy: String,
      @RequestParam(required = false, defaultValue = "true") asc: Boolean,
      @RequestParam(required = false, defaultValue = "10") limit: Int) =
      keyPhraseService.findByKeyPhrase(keyPhrase, asc, limit, OUT_CITATIONS, orderBy.toField())

  @GetMapping("/api/keyPhrases/{keyPhrase}/outCitations/count")
  override fun getOutCitationsOfPapersCountByKeyPhrase(
      @PathVariable(required = true) keyPhrase: String) =
      keyPhraseService.countByKeyPhrase(keyPhrase, OUT_CITATIONS)
}