package com.oidccall.createUserInAuth0.entities.listeners;

import java.time.LocalDate;

import com.oidccall.createUserInAuth0.entities.Template;

import jakarta.persistence.PrePersist;

public class TemplateEntityListener {

    @PrePersist
    public void setDefaultValidFrom(Template template) {
        if (template.getValidFrom() == null) {
            template.setValidFrom(LocalDate.now());
        }
    }
}
