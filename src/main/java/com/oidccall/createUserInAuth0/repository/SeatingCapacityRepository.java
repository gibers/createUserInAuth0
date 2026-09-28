package com.oidccall.createUserInAuth0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.SeatingCapacityId;

public interface SeatingCapacityRepository extends JpaRepository<SeatingCapacity, SeatingCapacityId> {

	List<SeatingCapacity> findAllByRestaurateur(Restaurateur restaurateur);

}
