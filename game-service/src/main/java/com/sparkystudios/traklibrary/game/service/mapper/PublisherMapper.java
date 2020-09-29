package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Publisher;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherDto publisherToPublisherDto(Publisher publisher);

    @Mapping(target = "games", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Publisher publisherDtoToPublisher(PublisherDto publisherDto);
}
