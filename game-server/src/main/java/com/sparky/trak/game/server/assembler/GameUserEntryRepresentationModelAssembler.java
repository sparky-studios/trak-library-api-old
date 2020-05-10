package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.server.controller.GameController;
import com.sparky.trak.game.server.controller.GameUserEntryController;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameUserEntryRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameUserEntryDto> {

    @Override
    public void addLinks(EntityModel<GameUserEntryDto> resource) {
        GameUserEntryDto content = resource.getContent();

        URI uri = (URI)RequestContextHolder
                .getRequestAttributes()
                .getAttribute("org.springframework.hateoas.server.mvc.UriComponentsBuilderFactory#BUILDER_CACHE", 0);

        String url = UriComponentsBuilder.newInstance()
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .build()
                .toUriString();

        if (content != null) {
            resource.add(linkTo(methodOn(GameUserEntryController.class).findById(content.getId()))
                    .withSelfRel());
            resource.add(linkTo(methodOn(GameController.class).findById(content.getGameId()))
                    .withRel("game"));
            resource.add(new Link(url + "/api/image-management/v1/images/games/{id}")
                    .withRel(LinkRelation.of("image"))
                    .expand(content.getGameId()));
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GameUserEntryDto>> resources) {

    }
}
