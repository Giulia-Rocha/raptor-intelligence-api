package com.ford.raptorapi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.DockerClientFactory;

/**
 * Base dos testes de integração: sobe um único Postgres real em container
 * (Testcontainers) com o mesmo schema.sql da aplicação e o compartilha por
 * toda a JVM de testes. Conecta o DataSource via DynamicPropertySource para
 * garantir um contexto por suíte.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseTest {

    static final PostgreSQLContainer<?> POSTGRES = createPostgres();

    private static PostgreSQLContainer<?> createPostgres() {
        if (!DockerClientFactory.instance().isDockerAvailable()) {
            throw new IllegalStateException(
                    "Docker is required to run the integration tests (Testcontainers).");
        }
        PostgreSQLContainer<?> container =
                new PostgreSQLContainer<>("postgres:16-alpine").withInitScript("schema.sql");
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }
}