package com.oidccall.createUserInAuth0.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.UsersDeleted;

public interface UsersDeletedRepository extends JpaRepository<UsersDeleted, Long> {
	Optional<UsersDeleted> findByAuth0UserId(String userId);

}
