package leets.leenk.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
public class MysqlTestConfig {

    private static final String MYSQL_IMAGE = "mysql:8.0.41";

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>(MYSQL_IMAGE)
                .withDatabaseName("testdb");
    }
}
