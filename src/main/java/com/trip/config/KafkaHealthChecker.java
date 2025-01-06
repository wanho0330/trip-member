package com.trip.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaHealthChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(KafkaHealthChecker.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Override
    public void run(String... args) throws Exception {
        Properties config = new Properties();

        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        config.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000");

        try (AdminClient adminClient = AdminClient.create(config)) {
            adminClient.listTopics().names().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Kafka connection failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

}
