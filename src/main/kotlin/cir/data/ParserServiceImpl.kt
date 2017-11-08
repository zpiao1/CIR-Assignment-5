package cir.data

import cir.data.entity.Paper
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ParserServiceImpl @Autowired constructor(
    private val paperWriteRepository: PaperWriteRepository,
    private val paperReadRepository: PaperReadRepository
) : ParserService {

  companion object {
    private val logger = LoggerFactory.getLogger(ParserServiceImpl::class.java)
  }

  @Value("\${dataset.path}")
  private lateinit var datasetResource: Resource

  @PostConstruct
  override fun parse() {
    if (paperReadRepository.hasData()) {
      return
    }
    parseEachPaper()
    fixRelations()
  }

  private fun parseEachPaper() {
    val inputStream = datasetResource.inputStream
    val papers = mutableListOf<Paper>()
    inputStream.bufferedReader().useLines {
      it.map { JSONObject(it) }
          .forEachIndexed { index, paperJson ->
            val authorsArray = paperJson.getJSONArray("authors")
            val authors = (0 until authorsArray.length())
                .map { authorsArray.getJSONObject(it) }
                .map { it.getString("name") }
            val id = paperJson.getString("id")
            val keyPhrasesArray = paperJson.getJSONArray("keyPhrases")
            val keyPhrases = (0 until keyPhrasesArray.length())
                .map { keyPhrasesArray.getString(it) }
            val paperAbstract = paperJson.getString("paperAbstract")
            val pdfUrlsArray = paperJson.getJSONArray("pdfUrls")
            val pdfUrls = (0 until pdfUrlsArray.length())
                .map { pdfUrlsArray.getString(it) }
            val s2Url = paperJson.getString("s2Url")
            val title = paperJson.getString("title")
            val venue = paperJson.getString("venue")
            val year = paperJson.optInt("year", 0)

            // Leave out inCitations and outCitations for now
            val paper = Paper(authors = authors, id = id, keyPhrases = keyPhrases,
                paperAbstract = paperAbstract, pdfUrls = pdfUrls, s2Url = s2Url, title = title,
                venue = venue, year = year)
            papers += paper
            if (papers.size == 5000) {
              paperWriteRepository.insertPapers(papers)
              papers.clear()
              logger.info("Finished {}/200000 papers", index + 1)
            }
          }
    }
  }

  private fun fixRelations() {
    val ids = paperReadRepository.findAllPapersId().toHashSet()
    val inputStream = datasetResource.inputStream
    val papers = mutableListOf<Paper>()
    inputStream.bufferedReader().useLines {
      it.map { JSONObject(it) }
          .forEachIndexed { index, paperJson ->
            val id = paperJson.getString("id")
            val paper = paperReadRepository.findPaperById(id) ?: return@forEachIndexed
            val inCitationsArray = paperJson.getJSONArray("inCitations")
            val inCitations = (0 until inCitationsArray.length())
                .map { inCitationsArray.getString(it) }
                .filter { it in ids }
            val outCitationsArray = paperJson.getJSONArray("outCitations")
            val outCitations = (0 until outCitationsArray.length())
                .map { outCitationsArray.getString(it) }
                .filter { it in ids }
            val newPaper = Paper(id = paper.id, inCitations = inCitations,
                outCitations = outCitations)
            papers += newPaper
            if (papers.size == 5000) {
              paperWriteRepository.updatePapers(papers)
              papers.clear()
              logger.info("Fixed relations of {}/200000 papers", index + 1)
            }
          }
    }
  }
}