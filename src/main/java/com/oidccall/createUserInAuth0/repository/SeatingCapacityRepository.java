package com.oidccall.createUserInAuth0.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.SeatingCapacityId;

public interface SeatingCapacityRepository extends JpaRepository<SeatingCapacity, SeatingCapacityId> {

}
