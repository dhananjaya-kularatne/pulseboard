package com.pulseboard.pulseboard_api.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * Explicitly configures the MongoDB connection and target database.
 *
 * This bean exists to work around a Spring Boot MongoDB auto-configuration
 * quirk: even with spring.data.mongodb.host/port/database correctly set in
 * application.properties (verified directly via Environment.getProperty),
 * the auto-configured MongoTemplate silently fell back to MongoDB's
 * driver-level default database ("test") instead of the configured one.
 *
 * Defining MongoDatabaseFactory and MongoTemplate explicitly here removes
 * that ambiguity entirely, while still reading the host/port/database from
 * application.properties so the connection remains environment-configurable
 * (e.g. for production deployment).
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private String port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        String connectionString = "mongodb://" + host + ":" + port;
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(connectionString),database);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}