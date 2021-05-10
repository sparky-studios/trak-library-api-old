package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.PlatformController;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
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
public class PlatformRepresentationModelAssembler implements SimpleRepresentationModelAssembler<PlatformDto> {

    private final PagedResourcesAssembler<GameDto> gameDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<PlatformDto> resource) {
        PlatformDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(PlatformController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(PlatformController.class)
                    .findGamesByPlatformId(content.getId(), Pageable.unpaged(), gameDtoPagedResourcesAssembler))
                    .withRel("games"));

            resource.add(linkTo(methodOn(PlatformController.class)
                    .findPlatformImageByPlatformId(content.getId()))
                    .withRel("image"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<PlatformDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
