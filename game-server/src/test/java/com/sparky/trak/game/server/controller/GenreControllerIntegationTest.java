package com.sparky.trak.game.server.controller;

import com.sparky.trak.game.service.dto.GenreDto;
import com.sparky.trak.game.server.exception.ApiError;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.json.Json;
import javax.json.JsonMergePatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenreControllerIntegationTest {

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

        GenreDto genreDto = new GenreDto();
        genreDto.setName(null);
        genreDto.setDescription("test-description");

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres", port), HttpMethod.POST, new HttpEntity<>(genreDto, httpHeaders), typeReference);

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
    public void save_withExistingGenreDto_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("test-name");
        genreDto.setDescription("test-description");

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres", port), HttpMethod.POST, new HttpEntity<>(genreDto, httpHeaders), typeReference);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Status code should be 400 (BAD_REQUEST).");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatus(), "The status code of the ApiError should be 400 (BAD_REQUEST).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void save_withValidRequestBody_returnsSavedGenreDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GenreDto>> typeReference = new ParameterizedTypeReference<EntityModel<GenreDto>>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GenreDto genreDto = new GenreDto();
        genreDto.setName("test-name");
        genreDto.setDescription("test-description");

        // Act
        ResponseEntity<EntityModel<GenreDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres", port), HttpMethod.POST, new HttpEntity<>(genreDto, httpHeaders), typeReference);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GenreDto result = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "Status code should be 202 (CREATED).");
        Assertions.assertTrue(result.getId() > 0, "The ID of the GenreDto has not been generated on save.");
        Assertions.assertEquals(genreDto.getName(), result.getName(), "The name should not be changed on save.");
        Assertions.assertEquals(genreDto.getDescription(), result.getDescription(), "The description should not be changed on save.");
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
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres/{id}", port), HttpMethod.GET, new HttpEntity<>(httpHeaders), typeReference, 1000L);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void findById_withValidId_returnsGenreDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GenreDto>> typeReference = new ParameterizedTypeReference<EntityModel<GenreDto>>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        // Act
        ResponseEntity<EntityModel<GenreDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres/{id}", port), HttpMethod.GET, new HttpEntity<>(httpHeaders), typeReference, 1L);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GenreDto genreDto = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code should be 200 (OK).");
        Assertions.assertEquals(1L, genreDto.getId(), "The ID of the GenreDto does not match the one requested.");
    }

    @Test
    public void update_withInvalidRequestBody_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName(null);
        genreDto.setDescription("test-description");
        genreDto.setVersion(0L);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres", port), HttpMethod.PUT, new HttpEntity<>(genreDto, httpHeaders), typeReference);

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
    public void update_withNonExistentGenreDto_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1000L);
        genreDto.setName("test-name");
        genreDto.setDescription("test-description");
        genreDto.setVersion(0L);

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres", port), HttpMethod.PUT, new HttpEntity<>(genreDto, httpHeaders), typeReference);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void update_withValidRequestBody_returnsUpdatedGenreDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GenreDto>> typeReference = new ParameterizedTypeReference<EntityModel<GenreDto>>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypes.HAL_JSON);

        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("new-test-name");
        genreDto.setDescription("test-description");
        genreDto.setVersion(0L);

        // Act
        ResponseEntity<EntityModel<GenreDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres", port), HttpMethod.PUT, new HttpEntity<>(genreDto, httpHeaders), typeReference);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GenreDto result = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code should be 200 (OK).");
        Assertions.assertEquals(genreDto.getId(), result.getId(), "The ID of the GenreDto should not be changed on an update.");
        Assertions.assertEquals(genreDto.getName(), result.getName(), "The name should be updated on an update.");
        Assertions.assertEquals(genreDto.getDescription(), result.getDescription(), "The description should be updated on an update.");
        Assertions.assertEquals(genreDto.getVersion() + 1, result.getVersion(), "The version should be incremented upon an update.");
    }

    @Test
    public void patch_withInvalidRequestBody_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));

        JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("name", "")
                .add("description", "patched-description")
                .build());

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres/{id}", port), HttpMethod.PATCH, new HttpEntity<>(jsonMergePatch, httpHeaders), typeReference, 5L);

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
    public void patch_withIdNotMappedToGenreDto_returnsApiError() {
        // Arrange
        ParameterizedTypeReference<ApiError> typeReference = new ParameterizedTypeReference<ApiError>() {};

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));

        JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("name", "patched-title")
                .add("description", "patched-description")
                .build());

        // Act
        ResponseEntity<ApiError> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres/{id}", port), HttpMethod.PATCH, new HttpEntity<>(jsonMergePatch, httpHeaders), typeReference, 1000L);

        // Assert
        ApiError apiError = responseEntity.getBody();
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Status code should be 404 (NOT_FOUND).");

        Assertions.assertEquals(HttpStatus.NOT_FOUND, apiError.getStatus(), "The status code of the ApiError should be 404 (NOT_FOUND).");
        Assertions.assertNotNull(apiError.getTimestamp(), "There should be a timestamp with the ApiError.");
        Assertions.assertNotNull(apiError.getMessage(), "There should be a exception message with the ApiError.");
    }

    @Test
    public void patch_withValidPatchRequestBody_returnsPatchedGenreDto() {
        // Arrange
        ParameterizedTypeReference<EntityModel<GenreDto>> typeReference = new ParameterizedTypeReference<EntityModel<GenreDto>>() {};

        HttpHeaders getHttpHeaders = new HttpHeaders();
        getHttpHeaders.setContentType(MediaTypes.HAL_JSON);

        ResponseEntity<EntityModel<GenreDto>> originalGenreDto = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres/{id}", port), HttpMethod.GET, new HttpEntity<>(getHttpHeaders), typeReference, 5L);

        JsonMergePatch jsonMergePatch = Json.createMergePatch(Json.createObjectBuilder()
                .add("name", "patched-name")
                .add("description", "patched-description")
                .build());

        HttpHeaders patchHttpHeaders = new HttpHeaders();
        patchHttpHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));

        // Act
        ResponseEntity<EntityModel<GenreDto>> responseEntity = testRestTemplate
                .exchange(String.format("http://localhost:%d/api/v1/game-management/genres/{id}", port), HttpMethod.PATCH, new HttpEntity<>(jsonMergePatch, patchHttpHeaders), typeReference, 5L);

        // Assert
        Assertions.assertNotNull(responseEntity.getBody(), "The response from the endpoint should not be null.");
        Assertions.assertNotNull(responseEntity.getBody().getContent(), "The content of the response should not be null.");

        GenreDto original = originalGenreDto.getBody().getContent();
        GenreDto result = responseEntity.getBody().getContent();

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Status code should be 200 (OK).");
        Assertions.assertEquals(original.getId(), result.getId(), "A patch should not update the ID.");
        Assertions.assertEquals("patched-name", result.getName(), "The title of the GenreDto should be changed for this patch.");
        Assertions.assertEquals("patched-description", result.getDescription(), "The description of the GenreDto should be changed for this patch.");
        Assertions.assertEquals(original.getVersion() + 1, result.getVersion(), "The version should be incremented upon a patch.");
    }
}
