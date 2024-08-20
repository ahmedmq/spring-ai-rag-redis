package com.ahmedmq.rag

import org.springframework.ai.chat.client.ChatClient.Builder
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class ChatController(
    chatClientBuilder: Builder,
    val dataLoadingService: DataLoadingService,
    val vectorStore: VectorStore
) {

    val chatClient = chatClientBuilder.build()

    @GetMapping
    fun index(model: Model): String {
        return "index"
    }

    @PostMapping("load")
    fun load(model: Model, @RequestParam("file") file: MultipartFile): String {
        dataLoadingService.load(file.resource)
        model.addAttribute("result", "Data Loaded successfully")
        return "index"
    }

    @GetMapping("/rag/chat")
    fun query(model: Model, @RequestParam("question") question: String): String {
        val advisorSearchRequest = SearchRequest
            .defaults()
            .withTopK(2)
            .withSimilarityThreshold(0.7)

        val prompt = """
            Here is the question \n{question}\n
            Please respond in only Japanese
        """.trimIndent()

        val answer = chatClient.prompt()
            .user { u -> u.text(prompt).param("question", question) }
            .advisors(QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
            .call()
            .content()
        model.addAttribute("answer", answer)
        return "index"
    }

}