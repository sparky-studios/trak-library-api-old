package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.GameMode;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.dto.GameFiltersDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryFiltersDto;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * The {@link GameFilterService} is a simple service which is used to either retrieve information that can
 * be used to finely tune {@link com.sparkystudios.traklibrary.game.domain.Game} queries and filter or to
 * apply already filtered information to. The {@link GameFilterService} builds an additional layer of
 * abstraction over the persistence layer, as well encapsulating filter logic into {@link GameFiltersDto}
 * transfer objects.
 *
 * The {@link GameFilterService} still follows the practise in that it will not catch or handle exceptions thrown
 * by the persistence layer, instead it will wrap them in a more reasonable response and propagate the exception
 * to the callee.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
public interface GameFilterService {

    /**
     * Retrieves a {@link GameFiltersDto}, which contains various collections of data which can be used
     * to fine-tune a user defined filter when searching for {@link com.sparkystudios.traklibrary.game.domain.Game}'s
     * from the game library. The method will not throw any exceptions if the filter criteria are empty.
     *
     * @return A {@link GameFiltersDto} instance which contains data which can be used to filter different
     * game libraries.
     */
    GameFiltersDto getGameFilters();

    /**
     * Retrieves a {@link GameUserEntryFiltersDto}, which contains various collections of data which can be used
     * to fine-tune a user defined filter when searching for {@link com.sparkystudios.traklibrary.game.domain.GameUserEntry}'s
     * from their own personal libraries. The method will not throw any exceptions if the filter criteria are empty.
     *
     * @return A {@link GameUserEntryFiltersDto} instance which contains data which can be used to filter different
     * game libraries.
     */
    GameUserEntryFiltersDto getGameUserEntryFilters();

    /**
     * Retrieves an {@link Iterable} of all {@link GameDetailsDto} that matches the given criteria within the different provided
     * arguments. The results returned will be a single page of results. If no {@link GameDetailsDto} match the given criteria,
     * the method will return an empty {@link Iterable}.
     *
     * @param platformIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Platform} to search against.
     * @param genreIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Genre} to search against.
     * @param gameModes The {@link GameMode}'s to search against.
     * @param pageable Which page of {@link GameDetailsDto} results to retrieve.
     *
     * @return An {@link Iterable} of filtered {@link GameDetailsDto} that match the given criteria.
     */
    Iterable<GameDetailsDto> findGamesByFilters(Set<Long> platformIds,
                                                Set<Long> genreIds,
                                                Set<GameMode> gameModes,
                                                Pageable pageable);

    /**
     * Retrieves the total count of all games that will match the filtered criteria given by the different arguments. This method
     * is used when paging to retrieve the total of all {@link GameDetailsDto} that will match the given criteria, rather than a single
     * page of results.
     *
     * @param platformIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Platform} to search against.
     * @param genreIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Genre} to search against.
     * @param gameModes The {@link GameMode}'s to search against.
     *
     * @return A {@link Long} that contains the count of all {@link GameDetailsDto} that match the given criteria.
     */
    long countGamesByFilters(Set<Long> platformIds,
                             Set<Long> genreIds,
                             Set<GameMode> gameModes);

    /**
     * Retrieves an {@link Iterable} of all {@link GameUserEntryDto} that matches the given criteria within the different provided
     * arguments. The results returned will be a single page of results. If no {@link GameUserEntryDto} match the given criteria,
     * the method will return an empty {@link Iterable}.
     *
     * @param platformIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Platform} to search against.
     * @param genreIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Genre} to search against.
     * @param gameModes The {@link GameMode}'s to search against.
     * @param statuses The {@link com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus}'s to search against.
     * @param pageable Which page of {@link GameUserEntryDto} results to retrieve.
     *
     * @return An {@link Iterable} of filtered {@link GameDetailsDto} that match the given criteria.
     */
    Iterable<GameUserEntryDto> findGameUserEntriesByFilters(Set<Long> platformIds,
                                                            Set<Long> genreIds,
                                                            Set<GameMode> gameModes,
                                                            Set<GameUserEntryStatus> statuses,
                                                            Pageable pageable);

    /**
     * Retrieves the total count of all games that will match the filtered criteria given by the different arguments. This method
     * is used when paging to retrieve the total of all {@link GameUserEntryDto} that will match the given criteria, rather than a single
     * page of results.
     *
     * @param platformIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Platform} to search against.
     * @param genreIds The ID's of the {@link com.sparkystudios.traklibrary.game.domain.Genre} to search against.
     * @param gameModes The {@link GameMode}'s to search against.
     * @param statuses The {@link com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus}'s to search against.
     *
     * @return A {@link Long} that contains the count of all {@link GameDetailsDto} that match the given criteria.
     */
    long countGameUserEntriesByFilters(Set<Long> platformIds,
                                       Set<Long> genreIds,
                                       Set<GameMode> gameModes,
                                       Set<GameUserEntryStatus> statuses);
}
