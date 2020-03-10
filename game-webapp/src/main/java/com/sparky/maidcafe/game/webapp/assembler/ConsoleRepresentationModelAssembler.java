package com.sparky.maidcafe.game.webapp.assembler;

import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import com.sparky.maidcafe.game.webapp.controller.ConsoleController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ConsoleRepresentationModelAssembler implements SimpleRepresentationModelAssembler<ConsoleDto> {

    @Override
    public void addLinks(EntityModel<ConsoleDto> resource) {
        ConsoleDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(ConsoleController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<ConsoleDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
