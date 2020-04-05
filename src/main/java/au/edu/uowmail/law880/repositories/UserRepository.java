package au.edu.uowmail.law880.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import au.edu.uowmail.law880.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
