package com.oidccall.createUserInAuth0.repository;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.enums.EmailStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    
    Optional<Users> findByEmailAndDeletedIsFalse(String email);
    List<Users> findByEmailAndDeletedIsTrue(String email);
    List<Users> findByEmail(String email);

    Optional<Users> findByAuth0UserId(String userId);
    List<Users> findAllByEmailStatusAndCreatedAtBefore(EmailStatusEnum emailStatusEnum, Instant createdAt);

    Optional<Users> findByAuth0UserIdAndDeletedIsFalse(String userId);
    boolean existsByEmail(String email);
    boolean existsByAuth0UserIdAndDeletedIsFalse(String userId);
}
