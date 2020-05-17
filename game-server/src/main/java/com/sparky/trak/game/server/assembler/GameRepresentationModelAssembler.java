package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.service.dto.DeveloperDto;
import com.sparky.trak.game.service.dto.GameDto;
import com.sparky.trak.game.server.controller.GameController;
import com.sparky.trak.game.service.dto.PlatformDto;
import com.sparky.trak.game.service.dto.PublisherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class GameRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameDto> {

    private final PagedResourcesAssembler<PlatformDto> platformDtoPagedResourcesAssembler;
    private final PagedResourcesAssembler<DeveloperDto> developerDtoPagedResourcesAssembler;
    private final PagedResourcesAssembler<PublisherDto> publisherDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<GameDto> resource) {
        GameDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(GameController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(GameController.class).findPlatformsByGameId(content.getId(), Pageable.unpaged(), platformDtoPagedResourcesAssembler))
                    .withRel("platforms"));

            resource.add(linkTo(methodOn(GameController.class).findGenresByGameId(content.getId()))
                    .withRel("genres"));

            resource.add(linkTo(methodOn(GameController.class).findDevelopersByGameId(content.getId(), Pageable.unpaged(), developerDtoPagedResourcesAssembler))
                    .withRel("developers"));

            resource.add(linkTo(methodOn(GameController.class).findPublishersByGameId(content.getId(), Pageable.unpaged(), publisherDtoPagedResourcesAssembler))
                    .withRel("publishers"));
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GameDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
