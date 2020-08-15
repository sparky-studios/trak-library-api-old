package com.traklibrary.game.service.mapper;

import com.traklibrary.game.domain.Publisher;
import com.traklibrary.game.service.dto.PublisherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherMapper INSTANCE = Mappers.getMapper(PublisherMapper.class);

    PublisherDto publisherToPublisherDto(Publisher publisher);

    @Mapping(target = "gamePublisherXrefs", ignore = true)
    Publisher publisherDtoToPublisher(PublisherDto publisherDto);
}
