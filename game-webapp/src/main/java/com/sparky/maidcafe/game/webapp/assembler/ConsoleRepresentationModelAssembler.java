package com.sparky.maidcafe.game.webapp.assembler;

import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import com.sparky.maidcafe.game.service.dto.GameDto;
import com.sparky.maidcafe.game.webapp.controller.ConsoleController;
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
public class ConsoleRepresentationModelAssembler implements SimpleRepresentationModelAssembler<ConsoleDto> {

    private final PagedResourcesAssembler<GameDto> gameDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<ConsoleDto> resource) {
        ConsoleDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(ConsoleController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(ConsoleController.class)
                    .findGamesByConsoleId(content.getId(), Pageable.unpaged(), gameDtoPagedResourcesAssembler))
                    .withRel("games"));
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<ConsoleDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
