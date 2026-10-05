package com.oidccall.createUserInAuth0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oidccall.createUserInAuth0.entities.DinnerServiceCapacity;
import com.oidccall.createUserInAuth0.entities.ServiceCapacityId;

@Repository
public interface DinnerServiceCapacityRepository extends JpaRepository<DinnerServiceCapacity, ServiceCapacityId> {

	List<DinnerServiceCapacity> findByTemplateId(long templateId);

}
