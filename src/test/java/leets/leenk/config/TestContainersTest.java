package leets.leenk.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MysqlTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestContainersTest {

    @Autowired
    private MySQLContainer<?> mysqlContainer;


    @Test
    void MySQL_컨테이너_정상_동작_테스트() {
        // MySQL 컨테이너 테스트
        assertThat(mysqlContainer).isNotNull();
        assertThat(mysqlContainer.isRunning()).isTrue();
        assertThat(mysqlContainer.getDatabaseName()).isEqualTo("testdb");

        System.out.println("MySQL Container JDBC URL: " + mysqlContainer.getJdbcUrl());
    }
}
