package com.garment.wms.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Slf4j
public class DatabaseConfig {

    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        String dbUrl = databaseUrl;
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }

        HikariConfig config = new HikariConfig();

        if (dbUrl != null && !dbUrl.isBlank()) {
            log.info("Configuring DataSource from DATABASE_URL environment variable.");
            try {
                if (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://")) {
                    URI dbUri = new URI(dbUrl);
                    String userInfo = dbUri.getUserInfo();
                    String username = null;
                    String password = null;
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        username = parts[0];
                        password = parts[1];
                    } else if (userInfo != null) {
                        username = userInfo;
                    }

                    int port = dbUri.getPort() > 0 ? dbUri.getPort() : 5432;
                    String dbName = dbUri.getPath();
                    if (dbName != null && dbName.startsWith("/")) {
                        dbName = dbName.substring(1);
                    }

                    String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", dbUri.getHost(), port, dbName);
                    String query = dbUri.getQuery();
                    if (query != null && !query.isBlank()) {
                        jdbcUrl += "?" + query;
                    }

                    config.setJdbcUrl(jdbcUrl);
                    if (username != null) config.setUsername(username);
                    if (password != null) config.setPassword(password);
                    config.setDriverClassName("org.postgresql.Driver");
                    log.info("Configured PostgreSQL JDBC URL: jdbc:postgresql://{}:{}/{}", dbUri.getHost(), port, dbName);
                } else {
                    config.setJdbcUrl(dbUrl);
                    config.setDriverClassName("org.postgresql.Driver");
                }
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL, falling back to H2 Postgres mode: {}", e.getMessage());
                configureH2(config);
            }
        } else {
            log.info("No DATABASE_URL found. Initializing embedded H2 in PostgreSQL compatibility mode for local dev/testing.");
            configureH2(config);
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);

        return new HikariDataSource(config);
    }

    private void configureH2(HikariConfig config) {
        config.setJdbcUrl("jdbc:h2:mem:garmentwms;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
    }
}
