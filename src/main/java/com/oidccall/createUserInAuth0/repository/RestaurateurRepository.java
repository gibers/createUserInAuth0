package com.oidccall.createUserInAuth0.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Users;

public interface RestaurateurRepository extends JpaRepository<Restaurateur, Long> {

	Optional<Restaurateur> findByUsersId(long usersId);

	Optional<Restaurateur> findByUsers(Users users);
}
