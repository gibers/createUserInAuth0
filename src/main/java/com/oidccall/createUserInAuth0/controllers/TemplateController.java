package com.oidccall.createUserInAuth0.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oidccall.createUserInAuth0.dtos.front.TemplateIdNameDto;
import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
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

        templateService.addTemplateImpl(users.getId());
    }

    @GetMapping("/all/{userId}")
    public List<TemplateWithoutServiceCapacityDto> getAllTemplate(Authentication authentication, @PathVariable String userId) {
        if (!userId.equals(authentication.getName())) {
            throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
        }
        Users users = (Users) authentication.getDetails();
        return templateService.getAllTemplateImpl(users.getId());

    }

    @PutMapping("/changeName/{userId}")
    public void changeTemplateName(Authentication authentication, @PathVariable String userId,
            @RequestBody TemplateIdNameDto templateIdNameDto) {
        if (!userId.equals(authentication.getName())) {
            throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
        }
        Users users = (Users) authentication.getDetails();
        templateService.changeTemplateNameImpl(users.getId(), templateIdNameDto);
    }

}
