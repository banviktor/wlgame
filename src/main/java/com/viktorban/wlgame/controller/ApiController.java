package com.viktorban.wlgame.controller;

import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Extends the root API endpoint.
 */
@Component
public class ApiController implements ResourceProcessor<RepositoryLinksResource> {

    /**
     * Adds additional hyperlinks to the root API endpoint.
     *
     * {@inheritDoc}
     */
    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class).currentUser()).withRel("self"));
        }
        return resource;
    }

}
