package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.server.controller.GameRequestController;
import com.sparky.trak.game.service.dto.GameRequestDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameRequestRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameRequestDto> {

    @Override
    public void addLinks(EntityModel<GameRequestDto> resource) {
        GameRequestDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(GameRequestController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameRequestDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
