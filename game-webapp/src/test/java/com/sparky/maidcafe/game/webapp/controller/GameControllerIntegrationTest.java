package com.sparky.maidcafe.game.webapp.controller;

import com.sparky.maidcafe.game.domain.AgeRating;
import com.sparky.maidcafe.game.service.dto.GameDto;
import com.sparky.maidcafe.game.service.dto.GenreDto;
import com.sparky.maidcafe.game.webapp.exception.ApiError;
import com.sparky.maidcafe.game.webapp.seeder.GameSeeder;
import com.sparky.maidcafe.game.webapp.seeder.SeederRunner;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.json.Json;
import javax.json.JsonMergePatch;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeAll
    public void beforeAll() {
        HttpClient httpClient = HttpClientBuilder.create().build();

        testRestTemplate.getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Test
    public void save_withInvalidRequestBody_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GameDto gameDto = new GameDto();
        gameDto.setTitle(null);
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games", port), HttpMethod.POST, new HttpEntity<>(gameDto, httpHeaders), typeReference);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Status code should be 400 (BAD_REQUEST).");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatus(), "The status code of the ApiError should be 400 (BAD_REQUEST).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
        Assertions.assertFalse(apiError.getSubErrors().isEmpty(), "There should be validation errors within the ApiError.");
    }

    @Test
    public void save_withExistingGameDto_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setAgeRating(AgeRating.TEEN);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games", port), HttpMethod.POST, new HttpEntity<>(gameDto, httpHeaders), typeReference);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Status code should be 400 (BAD_REQUEST).");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatus(), "The status code of the ApiError should be 400 (BAD_REQUEST).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void save_withValidRequestBody_returnsSavedGameDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GameDto>> typeReference = new ParameterizedTypeReference<EntityModel<GameDto>>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GameDto gameDto = new GameDto();
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setAgeRating(AgeRating.TEEN);

        // Act
        ResponseEntity<EntityModel<GameDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games", port), HttpMethod.POST, new HttpEntity<>(gameDto, httpHeaders), typeReference);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GameDto result = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "Status code should be 202 (CREATED).");
        Assertions.assertTrue(result.getId() > 0, "The ID of the GameDto has not been generated on save.");
        Assertions.assertEquals(gameDto.getTitle(), result.getTitle(), "The title should not be changed on save.");
        Assertions.assertEquals(gameDto.getDescription(), result.getDescription(), "The description should not be changed on save.");
        Assertions.assertNotNull(result.getVersion(), "The version should be created upon save.");
    }

    @Test
    public void findById_withInvalidId_returnsApiErrorAndNotFoundErrorCode() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}", port), HttpMethod.GET, new HttpEntity<>(httpHeaders), typeReference, 1000L);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void findById_withValidId_returnsGameDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GameDto>> typeReference = new ParameterizedTypeReference<EntityModel<GameDto>>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        // Act
        ResponseEntity<EntityModel<GameDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}", port), HttpMethod.GET, new HttpEntity<>(httpHeaders), typeReference, 1L);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GameDto gameDto = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code should be 200 (OK).");
        Assertions.assertEquals(1L, gameDto.getId(), "The ID of the GameDto does not match the one requested.");
    }

    @Test
    public void findGenresByGameId_withInvalidGameId_returnsApiErrorAndNotFoundErrorCode() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}/genres", port), HttpMethod.GET, new HttpEntity<>(httpHeaders), typeReference, 200L);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void update_withInvalidRequestBody_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        gameDto.setTitle(null);
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setVersion(0L);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games", port), HttpMethod.PUT, new HttpEntity<>(gameDto, httpHeaders), typeReference);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Status code should be 400 (BAD_REQUEST).");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatus(), "The status code of the ApiError should be 400 (BAD_REQUEST).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
        Assertions.assertFalse(apiError.getSubErrors().isEmpty(), "There should be validation errors within the ApiError.");
    }

    @Test
    public void update_withNonExistentGameDto_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GameDto gameDto = new GameDto();
        gameDto.setId(1000L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto.setVersion(0L);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games", port), HttpMethod.PUT, new HttpEntity<>(gameDto, httpHeaders), typeReference);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void update_withValidRequestBody_returnsUpdatedGameDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GameDto>> typeReference = new ParameterizedTypeReference<EntityModel<GameDto>>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto.setVersion(0L);

        // Act
        ResponseEntity<EntityModel<GameDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games", port), HttpMethod.PUT, new HttpEntity<>(gameDto, httpHeaders), typeReference);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GameDto result = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code should be 200 (OK).");
        Assertions.assertEquals(gameDto.getId(), result.getId(), "The ID of the GameDto should not be changed on an update.");
        Assertions.assertEquals(gameDto.getTitle(), result.getTitle(), "The title should be updated on an update.");
        Assertions.assertEquals(gameDto.getDescription(), result.getDescription(), "The description should be updated on an update.");
        Assertions.assertEquals(gameDto.getVersion() + 1, result.getVersion(), "The version should be incremented upon an update.");
    }

    @Test
    public void patch_withInvalidRequestBody_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));

        JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("title", "")
                .add("ageRating", AgeRating.EVERYONE.name())
                .build());

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}", port), HttpMethod.PATCH, new HttpEntity<>(jsonMergePatch, httpHeaders), typeReference, 5L);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Status code should be 400 (BAD_REQUEST).");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatus(), "The status code of the ApiError should be 400 (BAD_REQUEST).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
        Assertions.assertFalse(apiError.getSubErrors().isEmpty(), "There should be validation errors within the ApiError.");
    }

    @Test
    public void patch_withIdNotMappedToGameDto_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));

        JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("title", "patched-title")
                .add("ageRating", AgeRating.EVERYONE.name())
                .build());

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}", port), HttpMethod.PATCH, new HttpEntity<>(jsonMergePatch, httpHeaders), typeReference, 1000L);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void patch_withValidPatchRequestBody_returnsPatchedGameDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GameDto>> typeReference = new ParameterizedTypeReference<EntityModel<GameDto>>() {};

        HttpHeaders getHttpHeaders = new HttpHeaders();
        getHttpHeaders.setContentType(MediaTypes.HAL_JSON);

        ResponseEntity<EntityModel<GameDto>> originalGameDto = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}", port), HttpMethod.GET, new HttpEntity<>(getHttpHeaders), typeReference, 5L);

        JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("title", "patched-title")
                .add("description", "patched-description")
                .add("ageRating", AgeRating.EVERYONE.name())
                .build());

        HttpHeaders patchHttpHeaders = new HttpHeaders();
        patchHttpHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));

        // Act
        ResponseEntity<EntityModel<GameDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/games/{id}", port), HttpMethod.PATCH, new HttpEntity<>(jsonMergePatch, patchHttpHeaders), typeReference, 5L);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GameDto original = originalGameDto.getBody().getContent();
        GameDto result = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code should be 200 (OK).");
        Assertions.assertEquals(original.getId(), result.getId(), "A patch should not update the ID.");
        Assertions.assertEquals("patched-title", result.getTitle(), "The title of the GameDto should be changed for this patch.");
        Assertions.assertEquals("patched-description", result.getDescription(), "The description of the GameDto should be changed for this patch.");
        Assertions.assertEquals(original.getReleaseDate(), result.getReleaseDate(), "The release date should be unchanged for this patch.");
        Assertions.assertEquals(AgeRating.EVERYONE, result.getAgeRating(), "The age rating should should be changed for this patch.");
        Assertions.assertEquals(original.getVersion() + 1, result.getVersion(), "The version should be incremented upon a patch.");
    }
}
