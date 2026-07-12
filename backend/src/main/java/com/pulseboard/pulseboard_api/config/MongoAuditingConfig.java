package com.pulseboard.pulseboard_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Enables automatic population of Spring Data auditing annotations(@CreatedDate, @LastModifiedDate) on MongoDB documents.
 * Currently used by User.createdAt; future models can opt in as needed.
 */
@Configuration
@EnableMongoAuditing
public class MongoAuditingConfig {
}