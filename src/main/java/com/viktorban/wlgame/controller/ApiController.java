package com.viktorban.wlgame.controller;

import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ApiController implements ResourceProcessor<RepositoryLinksResource> {
    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class).currentUser()).withRel("current_user"));
        }
        return resource;
    }
}
