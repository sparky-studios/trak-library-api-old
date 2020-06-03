package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.server.controller.GameController;
import com.sparky.trak.game.server.controller.GameUserEntryController;
import com.sparky.trak.game.server.controller.PlatformController;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameUserEntryRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameUserEntryDto> {

    @Override
    public void addLinks(EntityModel<GameUserEntryDto> resource) {
        GameUserEntryDto content = resource.getContent();

        if (content != null) {
            resource.add(linkTo(methodOn(GameUserEntryController.class).findById(content.getId()))
                    .withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).findById(content.getGameId()))
                    .withRel("game"));
            resource.add(linkTo(methodOn(PlatformController.class).findById(content.getPlatformId()))
                    .withRel("platform"));
            resource.add(linkTo(methodOn(GameController.class).findGameImageByGameId(content.getGameId()))
                    .withRel("image"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameUserEntryDto>> resources) {

    }
}
