package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.GameController;
import com.sparkystudios.traklibrary.game.service.dto.GameInfoDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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
public class GameInfoRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameInfoDto> {

    private final PagedResourcesAssembler<GameUserEntryDto> gameUserEntryDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<GameInfoDto> resource) {
        GameInfoDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(WebMvcLinkBuilder.linkTo(methodOn(GameController.class).findGameInfoByGameId(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(GameController.class).findGameImageByGameId(content.getId()))
                    .withRel("image"));

            resource.add(linkTo(methodOn(GameController.class).findPlatformsByGameId(content.getId()))
                    .withRel("platforms"));

            resource.add(linkTo(methodOn(GameController.class).findGenresByGameId(content.getId()))
                    .withRel("genres"));

            resource.add(linkTo(methodOn(GameController.class).findDevelopersByGameId(content.getId()))
                    .withRel("developers"));

            resource.add(linkTo(methodOn(GameController.class).findPublishersByGameId(content.getId()))
                    .withRel("publishers"));

            resource.add(linkTo(methodOn(GameController.class).findGameUserEntriesByGameId(content.getId(), Pageable.unpaged(), gameUserEntryDtoPagedResourcesAssembler))
                    .withRel("entries"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameInfoDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
