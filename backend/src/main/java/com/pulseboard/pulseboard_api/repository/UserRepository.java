package com.pulseboard.pulseboard_api.repository;

import com.pulseboard.pulseboard_api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for User documents.
 * Extends MongoRepository to get standard CRUD operations for free, plus two custom lookup methods used during registration and login.
 */
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}