package cir.data

import cir.data.entity.Paper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.BulkOperations.BulkMode.ORDERED
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class PaperRepositoryImpl @Autowired constructor(
    private val mongoOperations: MongoOperations
) : PaperReadRepository, PaperWriteRepository {

  override fun savePaper(paper: Paper) {
    mongoOperations.save(paper)
  }

  override fun findPaperById(id: String): Paper? {
    return mongoOperations.findById(id, Paper::class.java)
  }

  override fun findPapersByAuthorName(name: String): List<Paper> {
    return listOf()
  }

  override fun insertPapers(papers: List<Paper>) {
    mongoOperations.insertAll(papers)
  }

  override fun updatePapers(papers: List<Paper>) {
    val bulkOps = mongoOperations.bulkOps<Paper>(ORDERED)
    papers.forEach {
      val update = Update()
          .set("inCitations", it.inCitations)
          .set("outCitations", it.outCitations)
      bulkOps.updateOne(query(where("id").isEqualTo(it.id)), update)
    }
    bulkOps.execute()
  }

  override fun paperIdExists(id: String): Boolean {
    return mongoOperations.exists<Paper>(query(where("id").isEqualTo(id)))
  }

  override fun findAllPapersId(): List<String> {
    val query = Query()
    query.fields().include("id")
    return mongoOperations.find<Paper>(query).map { it.id }
  }

  override fun hasData(): Boolean {
    return mongoOperations.count<Paper>(Query()) > 0
  }
}