package cir.data

import cir.data.entity.Paper

interface PaperWriteRepository {
  fun savePaper(paper: Paper)
  fun insertPapers(papers: List<Paper>)
  fun updatePapers(papers: List<Paper>)
}