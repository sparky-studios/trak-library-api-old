package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Publisher;
import com.sparky.trak.game.service.dto.PublisherDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherMapper INSTANCE = Mappers.getMapper(PublisherMapper.class);

    PublisherDto publisherToPublisherDto(Publisher publisher);

    Publisher publisherDtoToPublisher(PublisherDto publisherDto);
}
