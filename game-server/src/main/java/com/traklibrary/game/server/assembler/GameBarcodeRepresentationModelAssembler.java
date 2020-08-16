package com.traklibrary.game.server.assembler;

import com.traklibrary.game.server.controller.GameController;
import com.traklibrary.game.server.controller.PlatformController;
import com.traklibrary.game.service.dto.GameBarcodeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class GameBarcodeRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameBarcodeDto> {

    @Override
    public void addLinks(EntityModel<GameBarcodeDto> resource) {
        GameBarcodeDto content = resource.getContent();

        if (content != null) {
            resource.add(WebMvcLinkBuilder.linkTo(methodOn(GameController.class).findById(content.getGameId()))
                    .withRel("game"));

            resource.add(linkTo(methodOn(GameController.class).findGameInfoByGameId(content.getGameId()))
                    .withRel("gameInfo"));

            resource.add(linkTo(methodOn(PlatformController.class).findById(content.getPlatformId()))
                    .withRel("platform"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameBarcodeDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
