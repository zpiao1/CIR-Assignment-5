package cir.api.controller

interface YearApi {
  fun doesYearExist(year: Int): Boolean

  fun getYears(
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getYearsCount(): Long

  fun getPapersByYear(
      year: Int,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getPapersCountByYear(year: Int): Long

  fun getVenuesOfPapersByYear(
      year: Int,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getVenuesOfPapersCountByYear(year: Int): Long

  fun getKeyPhrasesOfPapersByYear(
      year: Int,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getKeyPhrasesCountOfPapersByYear(year: Int): Long

  fun getAuthorsOfPapersByYear(
      year: Int,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getAuthorsOfPapersCountByYear(year: Int): Long

  fun getInCitationsOfPapersByYear(
      year: Int,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getInCitationsOfPapersCountByYear(year: Int): Long

  fun getOutCitationsOfPapersByYear(
      year: Int,
      orderBy: String,
      asc: Boolean,
      limit: Int): Any

  fun getOutCitationsOfPapersCountByYear(year: Int): Long
}