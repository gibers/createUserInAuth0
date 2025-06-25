package com.oidccall.createUserInAuth0.repository;

import com.oidccall.createUserInAuth0.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    
    Optional<Users> findByEmail(String email);
    Optional<Users> findByAuth0UserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByAuth0UserId(String userId);
}
