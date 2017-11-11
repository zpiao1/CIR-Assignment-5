package cir.api.service

interface YearService {
  fun doesYearExist(year: Int): Boolean

  fun countYears(): Long

  fun countByYear(year: Int, field: String): Long

  fun findYears(asc: Boolean, limit: Int, orderBy: String): Any

  fun findByYear(year: Int, asc: Boolean, limit: Int, field: String, orderBy: String): Any
}