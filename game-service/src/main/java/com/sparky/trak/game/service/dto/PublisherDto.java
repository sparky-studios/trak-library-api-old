package com.sparky.trak.game.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Relation(collectionRelation = "data", itemRelation = "publisher")
public class PublisherDto extends CompanyDto {
}
