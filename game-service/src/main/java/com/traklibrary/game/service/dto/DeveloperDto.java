package com.traklibrary.game.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Relation(collectionRelation = "data", itemRelation = "developer")
public class DeveloperDto extends CompanyDto {
}
