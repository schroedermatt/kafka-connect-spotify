package com.jumptech.kafka.innertrack

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class StreamApplication

fun main(args: Array<String>) {
    SpringApplication.run(StreamApplication::class.java, *args)
}
