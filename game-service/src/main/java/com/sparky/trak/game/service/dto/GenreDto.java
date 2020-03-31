package com.sparky.trak.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Relation(collectionRelation = "data", itemRelation = "genre")
public class GenreDto {

    private long id;

    @NotEmpty(message = "{genre.validation.name.not-empty}")
    private String name;

    @Size(max = 4096, message = "{genre.validation.description.size}")
    private String description;

    private Long version;
}
