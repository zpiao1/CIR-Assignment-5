package cir.api.service

interface KeyPhrasesService {
  fun doesKeyPhraseExist(keyPhrase: String): Boolean

  fun countKeyPhrases(): Long

  fun countByKeyPhrase(keyPhrase: String, field: String): Long

  fun findKeyPhrases(asc: Boolean, limit: Int, orderBy: String): Any

  fun findByKeyPhrase(
      keyPhrase: String,
      asc: Boolean,
      limit: Int,
      field: String,
      orderBy: String
  ): Any
}