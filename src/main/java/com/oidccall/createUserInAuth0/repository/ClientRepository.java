package com.oidccall.createUserInAuth0.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
