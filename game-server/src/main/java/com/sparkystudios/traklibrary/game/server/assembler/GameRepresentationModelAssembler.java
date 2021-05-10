package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.domain.ImageSize;
import com.sparkystudios.traklibrary.game.server.controller.FranchiseController;
import com.sparkystudios.traklibrary.game.server.controller.GameController;
import com.sparkystudios.traklibrary.game.server.controller.GameImageController;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
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
public class GameRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameDto> {

    private final PagedResourcesAssembler<GameUserEntryDto> gameUserEntryDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<GameDto> resource) {
        GameDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(WebMvcLinkBuilder.linkTo(methodOn(GameController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(GameImageController.class).findGameImageByGameIdAndImageSize(content.getId(), ImageSize.SMALL))
                    .withRel("small_image"));

            resource.add(linkTo(methodOn(GameImageController.class).findGameImageByGameIdAndImageSize(content.getId(), ImageSize.MEDIUM))
                    .withRel("medium_image"));

            resource.add(linkTo(methodOn(GameImageController.class).findGameImageByGameIdAndImageSize(content.getId(), ImageSize.LARGE))
                    .withRel("large_image"));

            resource.add(linkTo(methodOn(GameController.class).findPlatformsByGameId(content.getId()))
                    .withRel("platforms"));

            resource.add(linkTo(methodOn(GameController.class).findGenresByGameId(content.getId()))
                    .withRel("genres"));

            resource.add(linkTo(methodOn(GameController.class).findDevelopersByGameId(content.getId()))
                    .withRel("developers"));

            resource.add(linkTo(methodOn(GameController.class).findPublishersByGameId(content.getId()))
                    .withRel("publishers"));

            resource.add(linkTo(methodOn(GameController.class).findGameUserEntriesByGameId(content.getId(), null, Pageable.unpaged(), gameUserEntryDtoPagedResourcesAssembler))
                    .withRel("entries"));

            resource.add(linkTo(methodOn(GameController.class).findDownloadableContentsByGameId(content.getId()))
                .withRel("downloadable_content"));

            resource.add(linkTo(methodOn(GameController.class).findGameDetailsByGameId(content.getId()))
                    .withRel("info"));

            if (content.getFranchiseId() != null) {
                resource.add(linkTo(methodOn(FranchiseController.class).findById(content.getFranchiseId()))
                        .withRel("franchise"));
            }
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GameDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
