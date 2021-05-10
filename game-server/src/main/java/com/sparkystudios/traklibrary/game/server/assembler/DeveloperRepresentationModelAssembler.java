package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.DeveloperController;
import com.sparkystudios.traklibrary.game.service.dto.DeveloperDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
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
public class DeveloperRepresentationModelAssembler implements SimpleRepresentationModelAssembler<DeveloperDto> {

    private final PagedResourcesAssembler<GameDto> gameDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<DeveloperDto> resource) {
        DeveloperDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(WebMvcLinkBuilder.linkTo(methodOn(DeveloperController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(linkTo(methodOn(DeveloperController.class).findGamesByDeveloperId(content.getId(), Pageable.unpaged(), gameDtoPagedResourcesAssembler))
                    .withRel("games"));

            resource.add(linkTo(methodOn(DeveloperController.class).findCompanyImageByCompanyId(content.getId()))
                    .withRel("image"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<DeveloperDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
