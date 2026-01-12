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
    fun mongoContainer(): MongoDBContainer {
        return MongoDBContainer(MONGO_IMAGE)
    }
}
// 투두: 테스트 컨테이너 관련 설정 코틀린으로 마이그레이션
