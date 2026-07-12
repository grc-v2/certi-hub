package com.certimaster;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the schema is fully reproducible from the repository: starts an empty
 * PostgreSQL 17 container, lets Flyway apply every migration (V1..Vn) and then
 * boots the full application context with {@code ddl-auto: validate}, so
 * Hibernate validates every JPA entity of every module (identity, learning,
 * engagement, productivity, plus the Spring Modulith event publication
 * registry) against the migrated schema. A missing table or column fails
 * context startup and therefore this test.
 *
 * <p>The container is managed through the plain {@code docker} CLI instead of
 * the Testcontainers API: the {@code org.testcontainers:testcontainers} core
 * jar on the test classpath cannot start containers without {@code commons-io},
 * which is not a project dependency. Driving the CLI keeps this test free of
 * additional dependencies; it is skipped in spirit (fails fast) when no Docker
 * daemon is available, just like a Testcontainers-based test would be.
 */
@SpringBootTest
class SchemaMigrationIntegrationTest {

    private static final String IMAGE = "postgres:17-alpine";
    private static final String DB = "certimaster";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private static final String CONTAINER_ID;
    private static final String JDBC_URL;

    static {
        CONTAINER_ID = exec("docker", "run", "-d", "--rm",
                "-e", "POSTGRES_DB=" + DB,
                "-e", "POSTGRES_USER=" + USERNAME,
                "-e", "POSTGRES_PASSWORD=" + PASSWORD,
                "-p", "127.0.0.1:0:5432",
                IMAGE).trim();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> exec("docker", "rm", "-f", CONTAINER_ID)));

        // First line looks like "127.0.0.1:55001".
        String mapping = exec("docker", "port", CONTAINER_ID, "5432/tcp").lines().findFirst().orElseThrow();
        int port = Integer.parseInt(mapping.substring(mapping.lastIndexOf(':') + 1).trim());
        JDBC_URL = "jdbc:postgresql://127.0.0.1:" + port + "/" + DB;

        awaitDatabase();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> JDBC_URL);
        registry.add("spring.datasource.username", () -> USERNAME);
        registry.add("spring.datasource.password", () -> PASSWORD);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        // No JWT keypair is shipped in the repo; boot with an ephemeral one.
        registry.add("jwt.generate-dev-keys", () -> "true");
        // Keep the test hermetic: don't talk to local Redis/SMTP from scheduled jobs.
        registry.add("app.mail.enabled", () -> "false");
        registry.add("auth.session.cleanup.enabled", () -> "false");
        registry.add("exam.session.auto-expire-enabled", () -> "false");
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void flywayMigrationsProduceSchemaThatPassesHibernateValidation() {
        List<String> failed = jdbcTemplate.queryForList(
                "SELECT version FROM flyway_schema_history WHERE NOT success", String.class);
        assertThat(failed).isEmpty();

        List<String> applied = jdbcTemplate.queryForList(
                "SELECT version FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank",
                String.class);
        assertThat(applied).contains("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    private static void awaitDatabase() {
        // During image init postgres starts, stops and restarts; wait until a
        // real query succeeds.
        IllegalStateException failure = new IllegalStateException("PostgreSQL container never became ready");
        for (int attempt = 0; attempt < 120; attempt++) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 Statement statement = connection.createStatement()) {
                statement.execute("SELECT 1");
                return;
            } catch (Exception e) {
                failure.addSuppressed(e);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw failure;
                }
            }
        }
        throw failure;
    }

    private static String exec(String... command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output = new String(process.getInputStream().readAllBytes());
            if (!process.waitFor(10, TimeUnit.MINUTES) || process.exitValue() != 0) {
                throw new IllegalStateException("Command failed: " + String.join(" ", command) + "\n" + output);
            }
            return output;
        } catch (IOException e) {
            throw new IllegalStateException("Docker is required for this test: " + String.join(" ", command), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while running: " + String.join(" ", command), e);
        }
    }
}
