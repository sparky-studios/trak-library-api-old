package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.GameMode;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import com.sparkystudios.traklibrary.game.server.assembler.GameDetailsRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameFilterRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryFilterRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.GameFilterService;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.dto.GameFiltersDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryFiltersDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The {@link GameFilterController} is a simple controller class that exposes a CRUD based API that is used to filter on
 * any entities or objects that pertain to games. It provides API end-points for retrieving and applying a filter to {@link GameDetailsDto}'s.
 * It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link GameFilterService}. The controllers primary purpose is to wrap the responses it received from the {@link GameFilterService}
 * into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON} response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.traklibrary.v1.hal+json")
public class GameFilterController {

    private final GameFilterService gameFilterService;
    private final GameFilterRepresentationModelAssembler gameFilterRepresentationModelAssembler;
    private final GameUserEntryFilterRepresentationModelAssembler gameUserEntryFilterRepresentationModelAssembler;
    private final GameDetailsRepresentationModelAssembler gameDetailsRepresentationModelAssembler;
    private final GameUserEntryRepresentationModelAssembler gameUserEntryRepresentationModelAssembler;

    /**
     * End-point that is used to retrieve a {@link GameFiltersDto}, which contains various collections of data which can
     * be used to fine-tune a user defined filter when searching for {@link com.sparkystudios.traklibrary.game.domain.Game}'s
     *  from the game library. The method will not throw any exceptions if the filter criteria are empty.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @return An {@link EntityModel} containing a {@link GameFiltersDto} instance which contains data which can be used to
     * filter different game libraries.
     */
    @AllowedForUser
    @GetMapping("/filters")
    public EntityModel<GameFiltersDto> getGameFilters() {
        return gameFilterRepresentationModelAssembler.toModel(gameFilterService.getGameFilters());
    }

    /**
     * End-point that is used to retrieve a {@link GameUserEntryFiltersDto}, which contains various collections of data which can
     * be used to fine-tune a user defined filter when searching for {@link com.sparkystudios.traklibrary.game.domain.GameUserEntry}'s
     * from the users personal library. The method will not throw any exceptions if the filter criteria are empty.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @return An {@link EntityModel} containing a {@link GameUserEntryFiltersDto} instance which contains data which can be used to
     * filter different user game entry libraries.
     */
    @AllowedForUser
    @GetMapping("/entries/filters")
    public EntityModel<GameUserEntryFiltersDto> getGameUserEntryFilters() {
        return gameUserEntryFilterRepresentationModelAssembler.toModel(gameFilterService.getGameUserEntryFilters());
    }

    /**
     * End-point that is used to retrieve an {@link Iterable} of all {@link GameDetailsDto} that matches the given criteria
     * within the provided arguments. The results returned will be a single page of results. The page and each
     * {@link GameDetailsDto} will be wrapped in a HATEOAS response. If no {@link GameDetailsDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param platformIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Platform} to search against.
     * @param genreIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Genre} to search against.
     * @param gameModes The {@link GameMode}'s to search against.
     * @param ageRatings The {@link AgeRating}'s to search against.
     * @param pageable Which page of {@link GameDetailsDto} results to retrieve.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GameDetailsDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing a {@link Iterable} of filtered {@link GameDetailsDto} that match the given criteria.
     */
    @AllowedForUser
    @GetMapping("/search")
    public PagedModel<EntityModel<GameDetailsDto>> findGamesByFilters(@RequestParam(name = "platform-ids", required = false) Set<Long> platformIds,
                                                                      @RequestParam(name = "genre-ids", required = false) Set<Long> genreIds,
                                                                      @RequestParam(name = "game-modes", required = false) Set<GameMode> gameModes,
                                                                      @RequestParam(name = "age-ratings", required = false) Set<AgeRating> ageRatings,
                                                                      @PageableDefault Pageable pageable,
                                                                      PagedResourcesAssembler<GameDetailsDto> pagedResourcesAssembler) {

        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDetailsDto> gameDetailsDtos = StreamSupport.stream(gameFilterService
                .findGamesByFilters(platformIds, genreIds, gameModes, ageRatings, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameFilterService.countGamesByFilters(platformIds, genreIds, gameModes, ageRatings);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameDetailsDtos, pageable, count), gameDetailsRepresentationModelAssembler, link);
    }

    /**
     * End-point that is used to retrieve an {@link Iterable} of all {@link GameUserEntryDto} that matches the given criteria
     * within the provided arguments. The results returned will be a single page of results. The page and each
     * {@link GameUserEntryDto} will be wrapped in a HATEOAS response. If no {@link GameUserEntryDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param platformIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Platform} to search against.
     * @param genreIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Genre} to search against.
     * @param gameModes The {@link GameMode}'s to search against.
     * @param ageRatings The {@link AgeRating}'s to search against.
     * @param statuses The {@link GameUserEntryStatus}'s to search against.
     * @param pageable Which page of {@link GameUserEntryDto} results to retrieve.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GameDetailsDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing a {@link Iterable} of filtered {@link GameUserEntryDto} that match the given criteria.
     */
    @AllowedForUser
    @GetMapping("/entries/search")
    public PagedModel<EntityModel<GameUserEntryDto>> findGameUserEntriesByFilters(@RequestParam(name = "platform-ids", required = false) Set<Long> platformIds,
                                                                                  @RequestParam(name = "genre-ids", required = false) Set<Long> genreIds,
                                                                                  @RequestParam(name = "game-modes", required = false) Set<GameMode> gameModes,
                                                                                  @RequestParam(name = "age-ratings", required = false) Set<AgeRating> ageRatings,
                                                                                  @RequestParam(required = false) Set<GameUserEntryStatus> statuses,
                                                                                  @PageableDefault Pageable pageable,
                                                                                  PagedResourcesAssembler<GameUserEntryDto> pagedResourcesAssembler) {

        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameUserEntryDto> gameUserEntryDtos = StreamSupport.stream(gameFilterService
                .findGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameFilterService.countGameUserEntriesByFilters(platformIds, genreIds, gameModes, ageRatings, statuses);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameUserEntryDtos, pageable, count), gameUserEntryRepresentationModelAssembler, link);
    }
}
