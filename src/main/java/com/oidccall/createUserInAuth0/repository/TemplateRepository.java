package com.oidccall.createUserInAuth0.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {

	Optional<Template> findAllByIdAndRestaurateur(long templateId, Restaurateur restaurateur);

}
