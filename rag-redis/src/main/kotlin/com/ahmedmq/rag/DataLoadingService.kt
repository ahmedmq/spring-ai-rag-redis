package com.ahmedmq.rag

import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service


@Service
class DataLoadingService(
    val vectorStore: VectorStore
) {
    fun load(resource: Resource) {
        val pdfReader = createPdfReader(resource)
        val splitDocuments = splitAndAnnotateDocuments(pdfReader, resource.filename)
        vectorStore.accept(splitDocuments)
    }

    private fun createPdfReader(resource: Resource) =
        PagePdfDocumentReader(
            resource,
            PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                    ExtractedTextFormatter.builder()
                        .withNumberOfBottomTextLinesToDelete(3)
                        .withNumberOfTopPagesToSkipBeforeDelete(1)
                        .build()
                )
                .withPagesPerDocument(1)
                .build()
        )

    private fun splitAndAnnotateDocuments(pdfReader: PagePdfDocumentReader, filename: String?) =
        TokenTextSplitter().apply(pdfReader.get()).onEach { document ->
            document.metadata["filename"] = filename
            document.metadata["version"] = 1
        }
}