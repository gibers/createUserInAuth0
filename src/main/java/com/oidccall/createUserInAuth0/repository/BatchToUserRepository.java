package com.oidccall.createUserInAuth0.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.BatchToUser;

public interface BatchToUserRepository extends JpaRepository<BatchToUser, Long> {
}
