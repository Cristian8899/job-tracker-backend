package com.jobtracker.repository;

import com.jobtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {  // ✅ Change Long -> UUID

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
