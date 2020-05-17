package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.server.controller.DeveloperController;
import com.sparky.trak.game.service.dto.DeveloperDto;
import com.sparky.trak.game.service.dto.GameDto;
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
public class DeveloperRepresentationModelAssembler implements SimpleRepresentationModelAssembler<DeveloperDto> {

    private final PagedResourcesAssembler<GameDto> gameDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<DeveloperDto> resource) {
        DeveloperDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(DeveloperController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(DeveloperController.class).findGamesByDeveloperId(content.getId(), Pageable.unpaged(), gameDtoPagedResourcesAssembler))
                    .withRel("games"));
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<DeveloperDto>> resources) {

    }
}
