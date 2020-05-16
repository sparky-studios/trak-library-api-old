package com.sparky.trak.game.server.assembler;

import com.sparky.trak.game.server.controller.CompanyController;
import com.sparky.trak.game.service.dto.CompanyDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompanyRepresentationModelAssembler implements SimpleRepresentationModelAssembler<CompanyDto> {

    @Override
    public void addLinks(EntityModel<CompanyDto> resource) {
        CompanyDto content = resource.getContent();

        // Only add content if a valid model has been provided.
        if (content != null) {
            resource.add(linkTo(methodOn(CompanyController.class).findById(content.getId()))
                    .withSelfRel());
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<CompanyDto>> resources) {
        // Needed for implementation purposes, but unused.
    }
}
