package com.sparkystudios.traklibrary.game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.Collections;

@DataJpaTest
class GameRequestTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullTitle_throwsPersistenceException() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setTitle(null);
        gameRequest.setNotes("notes");
        gameRequest.setCompleted(true);
        gameRequest.setCompletedDate(LocalDateTime.now());
        gameRequest.setUserId(1L);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameRequest));
    }

    @Test
    void persist_withTitleExceedingLength_throwsPersistenceException() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setTitle(String.join("", Collections.nCopies(300, "t")));
        gameRequest.setNotes("notes");
        gameRequest.setCompleted(true);
        gameRequest.setCompletedDate(LocalDateTime.now());
        gameRequest.setUserId(1L);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameRequest));
    }

    @Test
    void persist_withNotesExceedingLength_throwsPersistenceException() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setTitle("title");
        gameRequest.setNotes(String.join("", Collections.nCopies(1500, "t")));
        gameRequest.setCompleted(true);
        gameRequest.setCompletedDate(LocalDateTime.now());
        gameRequest.setUserId(1L);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(gameRequest));
    }

    @Test
    void persist_withValidGameRequest_mapsGameRequest() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setTitle("title");
        gameRequest.setNotes("notes");
        gameRequest.setCompleted(true);
        gameRequest.setCompletedDate(LocalDateTime.now());
        gameRequest.setUserId(1L);

        // GameImage
        GameRequest result = testEntityManager.persistFlushFind(gameRequest);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getTitle()).isEqualTo(gameRequest.getTitle());
        Assertions.assertThat(result.getNotes()).isEqualTo(gameRequest.getNotes());
        Assertions.assertThat(result.isCompleted()).isTrue();
        Assertions.assertThat(result.getCompletedDate()).isEqualToIgnoringNanos(gameRequest.getCompletedDate());
        Assertions.assertThat(result.getUserId()).isEqualTo(gameRequest.getUserId());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }
}
