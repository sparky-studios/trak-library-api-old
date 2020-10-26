package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.server.controller.FranchiseController;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class FranchiseRepresentationModelAssembler implements SimpleRepresentationModelAssembler<FranchiseDto> {

    private final PagedResourcesAssembler<GameDto> gameDtoPagedResourcesAssembler;

    @Override
    public void addLinks(EntityModel<FranchiseDto> resource) {
        FranchiseDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(WebMvcLinkBuilder.linkTo(methodOn(FranchiseController.class).findById(content.getId()))
                    .withSelfRel());

            resource.add(WebMvcLinkBuilder.linkTo(methodOn(FranchiseController.class).findGamesByFranchiseId(content.getId(), Pageable.unpaged(), gameDtoPagedResourcesAssembler))
                    .withRel("games"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<FranchiseDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
