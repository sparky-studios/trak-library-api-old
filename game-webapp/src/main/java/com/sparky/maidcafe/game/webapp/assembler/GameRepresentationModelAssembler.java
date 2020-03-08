package com.sparky.maidcafe.game.webapp.assembler;

import com.sparky.maidcafe.game.service.dto.GameDto;
import com.sparky.maidcafe.game.webapp.controller.GameController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameDto> {

    @Override
    public void addLinks(EntityModel<GameDto> resource) {
        GameDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(GameController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GameDto>> resources) {

    }
}
