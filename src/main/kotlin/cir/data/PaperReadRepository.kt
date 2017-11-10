package cir.data

import cir.data.entity.Paper

interface PaperReadRepository {
  fun hasData(): Boolean
  fun findPaperById(id: String): Paper?
  fun findAllPapersId(): List<String>
  fun paperIdExists(id: String): Boolean
  fun findPapersByAuthorName(name: String): List<Paper>
}