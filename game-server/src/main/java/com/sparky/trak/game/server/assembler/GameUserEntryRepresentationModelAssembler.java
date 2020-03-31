package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.service.dto.GameUserEntryDto;
import com.sparky.trak.game.server.controller.GameUserEntryController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameUserEntryRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameUserEntryDto> {

    @Override
    public void addLinks(EntityModel<GameUserEntryDto> resource) {
        GameUserEntryDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(GameUserEntryController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GameUserEntryDto>> resources) {

    }
}
