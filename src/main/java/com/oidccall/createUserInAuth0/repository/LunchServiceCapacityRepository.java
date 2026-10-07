package com.oidccall.createUserInAuth0.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oidccall.createUserInAuth0.entities.LunchServiceCapacity;
import com.oidccall.createUserInAuth0.entities.LunchServiceCapacityId;

@Repository
public interface LunchServiceCapacityRepository extends JpaRepository<LunchServiceCapacity, LunchServiceCapacityId> {

}
