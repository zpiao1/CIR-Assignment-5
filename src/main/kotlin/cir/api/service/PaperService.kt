package cir.api.service

import cir.data.entity.Paper

interface PaperService {

  fun doesPaperExist(id: String): Boolean

  fun countPapers(): Long

  fun countPapersByTitleContains(titleContains: String): Long

  fun countByPaper(id: String, field: String): Long

  fun findPapers(asc: Boolean, limit: Int, orderBy: String): List<Paper>

  fun findPaperById(id: String): Paper?

  fun findPapersByTitleContains(titleContains: String, asc: Boolean, limit: Int,
      orderBy: String): List<Paper>

  fun findByPaper(id: String, asc: Boolean, limit: Int, field: String, orderBy: String): Any
}