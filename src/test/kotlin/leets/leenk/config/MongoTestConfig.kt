package leets.leenk.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MongoDBContainer

private const val MONGO_IMAGE: String = "mongo:7.0"

@TestConfiguration
class MongoTestConfig {
    @Bean
    @ServiceConnection
    fun mongoContainer(): MongoDBContainer = MongoDBContainer(MONGO_IMAGE)
}
