package com.sparky.maidcafe.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Relation(collectionRelation = "consoles", itemRelation = "console")
public class ConsoleDto {

    private long id;

    @NotEmpty(message = "{console.validation.name.not-empty}")
    private String name;

    @Size(max = 4096, message = "{console.validation.description.size}")
    private String description;

    private LocalDate releaseDate;

    private Long version;
}
