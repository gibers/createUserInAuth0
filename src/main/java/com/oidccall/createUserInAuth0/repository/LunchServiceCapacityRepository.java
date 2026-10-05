package com.oidccall.createUserInAuth0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oidccall.createUserInAuth0.entities.LunchServiceCapacity;
import com.oidccall.createUserInAuth0.entities.ServiceCapacityId;

@Repository
public interface LunchServiceCapacityRepository extends JpaRepository<LunchServiceCapacity, ServiceCapacityId> {

	List<LunchServiceCapacity> findByTemplateId(long templateId);

}
