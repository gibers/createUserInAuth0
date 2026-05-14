package com.oidccall.createUserInAuth0.repository;

import com.oidccall.createUserInAuth0.entities.UsersDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersDeletedRepository extends JpaRepository<UsersDeleted, Long> {
    
}
