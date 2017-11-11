package cir.api.service

interface AuthorsService {

  fun doesAuthorExist(author: String): Boolean

  fun countAuthors(): Long

  fun countAuthorsByNameContains(nameContains: String): Long

  fun countByAuthor(author: String, field: String): Long

  fun findAuthors(asc: Boolean, limit: Int, orderBy: String): Any

  fun findAuthorsByNameContains(nameContains: String, asc: Boolean, limit: Int,
      orderBy: String): Any

  fun findByAuthor(author: String, asc: Boolean, limit: Int, field: String, orderBy: String): Any
}