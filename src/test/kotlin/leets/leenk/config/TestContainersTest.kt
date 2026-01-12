package leets.leenk.config

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.MySQLContainer

@SpringBootTest
@Import(MysqlTestConfig::class, MongoTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestContainersTest(
    private val mysqlContainer: MySQLContainer<*>,
    private val mongoContainer: MongoDBContainer
) : StringSpec({

    "MySQL 컨테이너가 정상적으로 실행되어야 한다" {
        mysqlContainer shouldNotBe null
        mysqlContainer.isRunning shouldBe true
        mysqlContainer.databaseName shouldBe "testdb"

        println("MySQL Container JDBC URL: ${mysqlContainer.jdbcUrl}")
    }

    "MongoDB 컨테이너가 정상적으로 실행되어야 한다" {
        mongoContainer shouldNotBe null
        mongoContainer.isRunning shouldBe true

        println("MongoDB Container Connection String: ${mongoContainer.connectionString}")
        println("MongoDB Container Replica Set URL: ${mongoContainer.replicaSetUrl}")
    }
})
