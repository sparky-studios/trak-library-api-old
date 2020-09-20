package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.GameRequestController;
import com.sparkystudios.traklibrary.game.service.dto.GameRequestDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameRequestRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameRequestDto> {

    @Override
    public void addLinks(EntityModel<GameRequestDto> resource) {
        GameRequestDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(WebMvcLinkBuilder.linkTo(methodOn(GameRequestController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameRequestDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
