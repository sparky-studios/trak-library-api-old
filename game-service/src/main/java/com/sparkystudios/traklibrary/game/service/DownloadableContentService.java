package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;

/**
 * The {@link DownloadableContentService} follows the basic CRUD principle for interaction with {@link DownloadableContent}
 * entities on the persistence layer. However, the {@link DownloadableContentService} builds an additional layer of abstraction,
 * with the primary purpose being checking the validity of {@link DownloadableContent} data being requested from the persistence layer,
 * as well as encapsulating any domain-based objects into {@link DownloadableContentDto} transfer objects, for additional validation and protection.
 *
 * The {@link DownloadableContentService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
public interface DownloadableContentService {

    /**
     * Given an ID of a {@link DownloadableContent} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link DownloadableContent} entity that matches the given ID and map it to a {@link DownloadableContentDto}. If the ID provided does not
     * map to any known {@link DownloadableContent} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link DownloadableContent} entity to try and retrieve.
     *
     * @return The {@link DownloadableContent} entity matching the ID mapped to a {@link DownloadableContentDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't match an existing {@link DownloadableContent}.
     */
    DownloadableContentDto findById(long id);

    /**
     * Given an ID of a {@link GameDto} entity, this service method will retrieve all of the {@link DownloadableContentDto}s entities that are associated
     * with this {@link GameDto}. If no {@link DownloadableContent}s are associated with a given {@link Game}, then an empty {@link Iterable} is returned.
     * If a {@link Game} with the specified ID doesn't exist, then a {@link javax.persistence.EntityNotFoundException} exception will be thrown. The
     * {@link DownloadableContent}'s within the list are returned in name ascending order.
     *
     * @param gameId The ID of the {@link Game} to retrieve {@link DownloadableContent}s for.
     *
     * @return The {@link DownloadableContent} entities mapped to the {@link Game}, converted to {@link DownloadableContentDto}'s.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the gameId doesn't match any {@link Game} entities.
     */
    Iterable<DownloadableContentDto> findDownloadableContentsByGameId(long gameId);
}
