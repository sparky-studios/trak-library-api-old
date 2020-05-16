package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.service.dto.PlatformDto;
import com.sparky.trak.game.server.controller.PlatformController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class PlatformRepresentationModelAssembler implements SimpleRepresentationModelAssembler<PlatformDto> {

    @Override
    public void addLinks(EntityModel<PlatformDto> resource) {
        PlatformDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(PlatformController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(PlatformController.class)
                    .findGamesByPlatformId(content.getId(), Pageable.unpaged(), null))
                    .withRel("games"));
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<PlatformDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}