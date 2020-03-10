package com.sparky.maidcafe.game.service.mapper;

import com.sparky.maidcafe.game.domain.Console;
import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConsoleMapper {

    ConsoleMapper INSTANCE = Mappers.getMapper(ConsoleMapper.class);

    ConsoleDto consoleToConsoleDto(Console console);

    @Mapping(target = "gameConsoleXrefs", ignore = true)
    Console consoleDtoToConsole(ConsoleDto consoleDto);
}
