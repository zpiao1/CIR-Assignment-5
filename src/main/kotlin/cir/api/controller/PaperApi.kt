package cir.api.controller

import cir.data.entity.Paper

interface PaperApi {

  fun doesPaperExist(id: String): Boolean

  fun getPapers(
      titleContains: String?,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): List<Paper>

  fun getPapersCount(): Long

  fun getPaperById(id: String): Paper

  fun getAuthorsOfPaper(
      id: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getAuthorsCountOfPaper(id: String): Long

  fun getYearOfPaper(id: String): Any

  fun getKeyPhrasesOfPaper(
      id: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getKeyPhrasesCountOfPaper(id: String): Long

  fun getVenueOfPaper(id: String): Any

  fun getInCitationsOfPaper(
      id: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getInCitationsCountOfPaper(id: String): Long

  fun getOutCitationsOfPaper(
      id: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getOutCitationsCountOfPaper(id: String): Long
}