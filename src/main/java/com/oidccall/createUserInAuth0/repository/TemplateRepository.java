package com.oidccall.createUserInAuth0.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {

}
