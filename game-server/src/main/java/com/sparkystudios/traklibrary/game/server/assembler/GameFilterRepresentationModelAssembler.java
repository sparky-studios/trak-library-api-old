package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.GameFilterController;
import com.sparkystudios.traklibrary.game.service.dto.GameFiltersDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class GameFilterRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameFiltersDto> {

    @Override
    public void addLinks(EntityModel<GameFiltersDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(GameFilterController.class).getGameFilters())
                .withSelfRel());
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameFiltersDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
