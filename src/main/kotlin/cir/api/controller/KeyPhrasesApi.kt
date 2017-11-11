package cir.api.controller

interface KeyPhrasesApi {
  fun doesKeyPhraseExist(keyPhrase: String): Boolean

  fun getKeyPhrases(
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getKeyPhrasesCount(): Long

  fun getPapersByKeyPhrase(
      keyPhrase: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getPapersCountByKeyPhrase(keyPhrase: String): Long

  fun getYearsOfPapersByKeyPhrase(
      keyPhrase: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getYearsOfPapersCountByKeyPhrase(keyPhrase: String): Long

  fun getVenuesOfPapersByKeyPhrase(
      keyPhrase: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getVenuesOfPapersCountByKeyPhrase(keyPhrase: String): Long

  fun getAuthorsOfPapersByKeyPhrase(
      keyPhrase: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getAuthorsOfPapersCountByKeyPhrase(keyPhrase: String): Long

  fun getInCitationsOfPapersByKeyPhrase(
      keyPhrase: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getInCitationsOfPapersCountByKeyPhrase(keyPhrase: String): Long

  fun getOutCitationsOfPapersByKeyPhrase(
      keyPhrase: String,
      orderBy: String,
      asc: Boolean,
      limit: Int
  ): Any

  fun getOutCitationsOfPapersCountByKeyPhrase(keyPhrase: String): Long
}