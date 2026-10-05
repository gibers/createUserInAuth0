package com.oidccall.createUserInAuth0.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
import com.oidccall.createUserInAuth0.implementation.template.TemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/template")
@Slf4j
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/add/{userId}")
    public void addTemplate(Authentication authentication, @PathVariable String userId) {
        if (!userId.equals(authentication.getName())) {
            throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
        }
        Users users = (Users) authentication.getDetails();
        //        users.getId()

        templateService.addTemplateImpl(users.getId());
    }

    @GetMapping("/seatingCapacity/{userId}")
    public List<SeatingCapacityMax> getSeatingCapacity(Authentication authentication, @PathVariable String userId) {
        if (!userId.equals(authentication.getName())) {
            throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
        }
        Users users = (Users) authentication.getDetails();
        return templateService.getTemplateImpl(users.getId());
    }

}
