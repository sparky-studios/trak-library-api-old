package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.GameFilterController;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryFiltersDto;
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
public class GameUserEntryFilterRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameUserEntryFiltersDto> {

    @Override
    public void addLinks(EntityModel<GameUserEntryFiltersDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(GameFilterController.class).getGameUserEntryFilters())
                .withSelfRel());
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameUserEntryFiltersDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}