package com.traklibrary.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public abstract class CompanyDto {

    private long id;

    @NotEmpty(message = "{company.validation.title.not-empty}")
    private String name;

    @Size(max = 4096, message = "{company.validation.description.size}")
    private String description;

    @NotNull(message = "{company.validation.founded-date.not-null}")
    private LocalDate foundedDate;

    private Long version;
}
