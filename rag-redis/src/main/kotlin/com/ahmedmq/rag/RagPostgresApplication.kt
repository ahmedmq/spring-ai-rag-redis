package com.ahmedmq.rag

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RagPostgresApplication

fun main(args: Array<String>) {
    runApplication<RagPostgresApplication>(*args)
}
