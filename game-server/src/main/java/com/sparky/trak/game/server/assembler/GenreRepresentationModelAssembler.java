package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.service.dto.GameDto;
import com.sparky.trak.game.service.dto.GenreDto;
import com.sparky.trak.game.server.controller.GenreController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class GenreRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GenreDto> {

    private final PagedResourcesAssembler<GameDto> gameDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<GenreDto> resource) {
        GenreDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(GenreController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(GenreController.class).findGamesByGenreId(content.getId(), Pageable.unpaged(), gameDtoPagedResourcesAssembler))
                .withRel("games"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<GenreDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
