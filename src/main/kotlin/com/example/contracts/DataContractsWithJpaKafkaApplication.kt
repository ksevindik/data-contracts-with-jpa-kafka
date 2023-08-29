package com.example.contracts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DataContractsWithJpaKafkaApplication

fun main(args: Array<String>) {
	runApplication<DataContractsWithJpaKafkaApplication>(*args)
}
