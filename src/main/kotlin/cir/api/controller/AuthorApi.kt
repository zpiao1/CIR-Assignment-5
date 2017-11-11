package cir.api.controller

interface AuthorApi {

  fun doesAuthorExist(name: String): Boolean

  fun getAuthors(
      nameContains: String?,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getAuthorsCount(): Long

  fun getPapersByAuthor(
      name: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getPapersCountByAuthor(name: String): Long

  fun getYearsOfPapersByAuthor(
      name: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getYearsOfPapersCountByAuthor(name: String): Long

  fun getKeyPhrasesOfPapersByAuthor(
      name: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getKeyPhrasesCountOfPapersByAuthor(name: String): Long

  fun getVenuesOfPapersByAuthor(
      name: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getVenuesOfPapersCountByAuthor(name: String): Long

  fun getInCitationsOfPapersByAuthor(
      name: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getInCitationsOfPapersCountByAuthor(name: String): Long

  fun getOutCitationsOfPapersByAuthor(
      name: String,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getOutCitationsOfPapersCountByAuthor(name: String): Long
}