package cir.api.controller

import cir.data.entity.Paper

interface AuthorApi {

  fun doesAuthorExist(name: String): Boolean

  fun getAuthors(
      nameContains: String?,
      orderBy: String = "name",
      asc: Boolean = true,
      limit: Int = 100): Any

  fun getAuthorsCount(): Long

  fun getPapersByAuthor(
      name: String,
      orderBy: String = "title",
      asc: Boolean = true,
      limit: Int = 20): List<Paper>

  fun getPapersCountByAuthor(name: String): Long

  fun getYearsOfPapersByAuthor(
      name: String,
      orderBy: String = "year",
      asc: Boolean = true,
      limit: Int = 100): Any

  fun getYearsOfPapersCountByAuthor(name: String): Long

  fun getKeyPhrasesOfPapersByAuthor(
      name: String,
      orderBy: String = "keyPhrase",
      asc: Boolean = true,
      limit: Int = 100): Any

  fun getKeyPhrasesCountOfPapersByAuthor(name: String): Long

  fun getVenuesOfPapersByAuthor(
      name: String,
      orderBy: String = "venue",
      asc: Boolean = true,
      limit: Int = 100): Any

  fun getVenuesCountOfPapersByAuthor(name: String): Long

  fun getInCitationsOfPapersByAuthor(
      name: String,
      orderBy: String = "title",
      asc: Boolean = true,
      limit: Int = 20): List<Paper>

  fun getInCitationsCountOfPapersByAuthor(name: String): Long

  fun getOutCitationsOfPapersByAuthor(
      name: String,
      orderBy: String = "title",
      asc: Boolean = true,
      limit: Int = 20): List<Paper>

  fun getOutCitationsCountOfPapersByAuthor(name: String): Long
}