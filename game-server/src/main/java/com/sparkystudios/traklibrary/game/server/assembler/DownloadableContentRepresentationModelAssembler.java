package com.sparkystudios.traklibrary.game.server.assembler;

import com.sparkystudios.traklibrary.game.domain.ImageSize;
import com.sparkystudios.traklibrary.game.server.controller.DownloadableContentController;
import com.sparkystudios.traklibrary.game.server.controller.DownloadableContentImageController;
import com.sparkystudios.traklibrary.game.server.controller.GameController;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class DownloadableContentRepresentationModelAssembler implements SimpleRepresentationModelAssembler<DownloadableContentDto> {
    @Override
    public void addLinks(EntityModel<DownloadableContentDto> resource) {
        DownloadableContentDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(DownloadableContentController.class).findById(content.getId()))
                    .withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).findById(content.getGameId()))
                    .withRel("game"));
            resource.add(linkTo(methodOn(DownloadableContentImageController.class).findDownloadableContentImageByDownloadableContentIdAndImageSize(content.getId(), ImageSize.SMALL))
                    .withRel("small_image"));
            resource.add(linkTo(methodOn(DownloadableContentImageController.class).findDownloadableContentImageByDownloadableContentIdAndImageSize(content.getId(), ImageSize.MEDIUM))
                    .withRel("medium_image"));
            resource.add(linkTo(methodOn(DownloadableContentImageController.class).findDownloadableContentImageByDownloadableContentIdAndImageSize(content.getId(), ImageSize.LARGE))
                    .withRel("large_image"));
        }
    }

    @Override
    public void addLinks(@NonNull CollectionModel<EntityModel<DownloadableContentDto>> resources) {
        // Unused. Additional resource links aren't added to collections.
    }
}
