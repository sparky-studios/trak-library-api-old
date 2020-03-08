package com.sparky.maidcafe.game.webapp.assembler;

import com.sparky.maidcafe.game.service.dto.GenreDto;
import com.sparky.maidcafe.game.webapp.controller.GenreController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GenreRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GenreDto> {

    @Override
    public void addLinks(EntityModel<GenreDto> resource) {
        GenreDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(GenreController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GenreDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
