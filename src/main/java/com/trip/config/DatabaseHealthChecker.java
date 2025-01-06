package com.trip.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DatabaseHealthChecker implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("SELECT 1");
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
