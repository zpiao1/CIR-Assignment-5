package cir.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * InCitations and outCitations will be saved later
 */
@Document
data class Paper(
    val authors: List<String> = listOf(),
    @Id val id: String = "",
    val inCitations: List<String> = listOf(),
    val outCitations: List<String> = listOf(),
    val keyPhrases: List<String> = listOf(),
    val paperAbstract: String = "",
    val pdfUrls: List<String> = listOf(),
    val s2Url: String = "",
    val title: String = "",
    val venue: String = "",
    val year: Int = 0
)