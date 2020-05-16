package com.sparky.trak.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Relation(collectionRelation = "data", itemRelation = "platform")
public class PlatformDto {

    private long id;

    @NotEmpty(message = "{platform.validation.name.not-empty}")
    private String name;

    @Size(max = 4096, message = "{platform.validation.description.size}")
    private String description;

    private LocalDate releaseDate;

    private Long version;
}
