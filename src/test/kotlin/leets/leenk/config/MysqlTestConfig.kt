package leets.leenk.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer

private const val MYSQL_IMAGE: String = "mysql:8.0.41"

@TestConfiguration
class MysqlTestConfig {
    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> {
        return MySQLContainer(MYSQL_IMAGE)
            .withDatabaseName("testdb")
    }
}
