package com.sparky.trak.game.server.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import com.sparky.trak.game.domain.AgeRating;
import com.sparky.trak.game.server.assembler.PlatformRepresentationModelAssembler;
import com.sparky.trak.game.server.assembler.GameRepresentationModelAssembler;
import com.sparky.trak.game.server.exception.GlobalExceptionHandler;
import com.sparky.trak.game.service.PlatformService;
import com.sparky.trak.game.service.GameService;
import com.sparky.trak.game.service.dto.PlatformDto;
import com.sparky.trak.game.service.dto.GameDto;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlatformControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlatformService platformService;

    @Mock
    private GameService gameService;

    @Spy
    private PlatformRepresentationModelAssembler platformRepresentationModelAssembler;

    @Spy
    private GameRepresentationModelAssembler gameRepresentationModelAssembler;

    @InjectMocks
    private PlatformController platformController;

    @BeforeAll
    public void beforeAll() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(platformController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new SpecificationArgumentResolver())
                .build();
    }

    @AfterEach
    public void afterEach() {
        Mockito.reset(platformService);
    }

    @Test
    public void save_withNullPlatformDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/v1/platforms")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(content().contentType(MediaTypes.HAL_JSON));
        result.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.debugMessage").isEmpty());
        result.andExpect(jsonPath("$.subErrors").isEmpty());
    }

    @Test
    public void save_withInvalidPlatformDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/v1/platforms")
                .content(new ObjectMapper().writeValueAsString(new PlatformDto()))
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.debugMessage").isEmpty());
        result.andExpect(jsonPath("$.subErrors").isNotEmpty());
        result.andExpect(jsonPath("$.subErrors[0].@type").value("ApiValidationError"));
    }

    @Test
    public void save_withValidPlatformDto_returnsPlatformDtoHateoasResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setName("platform-name");
        platformDto.setDescription("This sure is a description of a platform.");
        platformDto.setReleaseDate(LocalDate.now());
        platformDto.setVersion(0L);

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jackson2HalModule())
                .registerModule(new JSR353Module());

        Mockito.when(platformService.save(ArgumentMatchers.any()))
                .thenReturn(platformDto);

        // Act
        ResultActions result = mockMvc.perform(post("/v1/platforms")
                .content(objectMapper.writeValueAsString(platformDto))
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.releaseDate").exists());
        result.andExpect(jsonPath("$.version").exists());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));
        result.andExpect(jsonPath("$.links[1].rel").value("games"));
    }

    @Test
    public void findById_withInvalidId_returns404ApiError() throws Exception {
        // Arrange
        Mockito.when(platformService.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new EntityNotFoundException("Entity sure is missing."));

        // Act
        ResultActions result = mockMvc.perform(get("/v1/platforms/1")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isNotFound());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.debugMessage").isEmpty());
        result.andExpect(jsonPath("$.subErrors").isEmpty());
    }

    @Test
    public void findById_withValidId_returnsPlatformDtoHateoasResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(1L);
        platformDto.setName("platform-name");
        platformDto.setDescription("The description of the platform.");
        platformDto.setReleaseDate(LocalDate.now());
        platformDto.setVersion(2L);

        Mockito.when(platformService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(platformDto);

        // Act
        ResultActions result = mockMvc.perform(get("/v1/platforms/1")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.releaseDate").exists());
        result.andExpect(jsonPath("$.version").exists());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));
        result.andExpect(jsonPath("$.links[1].rel").value("games"));
    }

    @Test
    public void findGamesByPlatformId_withNoGames_returnsEmptyHateoasResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findGamesByPlatformId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions result = mockMvc.perform(get("/v1/platforms/1/games")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").isArray());
        result.andExpect(jsonPath("$.content").isEmpty());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));

        result.andExpect(jsonPath("$.page.size").value(10));
        result.andExpect(jsonPath("$.page.totalElements").value(0));
        result.andExpect(jsonPath("$.page.totalPages").value(0));
        result.andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void findGamesByPlatformId_withGames_returnsGameDtoHateoasResponse() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("game-title-one");
        gameDto1.setDescription("Game description");
        gameDto1.setAgeRating(AgeRating.ADULTS_ONLY);
        gameDto1.setReleaseDate(LocalDate.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("game-title-two");
        gameDto2.setDescription("Game description");
        gameDto2.setAgeRating(AgeRating.TEEN);
        gameDto2.setReleaseDate(LocalDate.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findGamesByPlatformId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        // Act
        ResultActions result = mockMvc.perform(get("/v1/platforms/1/games")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));

        result.andExpect(jsonPath("$.content").isArray());
        result.andExpect(jsonPath("$.content").isNotEmpty());

        result.andExpect(jsonPath("$.content[0].id").value(gameDto1.getId()));
        result.andExpect(jsonPath("$.content[0].title").value(gameDto1.getTitle()));
        result.andExpect(jsonPath("$.content[0].description").value(gameDto1.getDescription()));
        result.andExpect(jsonPath("$.content[0].releaseDate").exists());
        result.andExpect(jsonPath("$.content[0].ageRating").value(gameDto1.getAgeRating().name()));
        result.andExpect(jsonPath("$.content[0].version").value(gameDto1.getVersion()));

        result.andExpect(jsonPath("$.content[0].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[0].links[1].rel").value("platforms"));
        result.andExpect(jsonPath("$.content[0].links[2].rel").value("genres"));

        result.andExpect(jsonPath("$.content[1].id").value(gameDto2.getId()));
        result.andExpect(jsonPath("$.content[1].title").value(gameDto2.getTitle()));
        result.andExpect(jsonPath("$.content[1].description").value(gameDto2.getDescription()));
        result.andExpect(jsonPath("$.content[1].releaseDate").exists());
        result.andExpect(jsonPath("$.content[1].ageRating").value(gameDto2.getAgeRating().name()));
        result.andExpect(jsonPath("$.content[1].version").value(gameDto2.getVersion()));

        result.andExpect(jsonPath("$.content[1].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[1].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[1].links[1].rel").value("platforms"));
        result.andExpect(jsonPath("$.content[1].links[2].rel").value("genres"));

        result.andExpect(jsonPath("$.page.size").value(10));
        result.andExpect(jsonPath("$.page.totalElements").value(2));
        result.andExpect(jsonPath("$.page.totalPages").value(1));
        result.andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void findAll_withNoPlatforms_returnsEmptyHateoasResponse() throws Exception {
        // Arrange
        Mockito.when(platformService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions result = mockMvc.perform(get("/v1/platforms")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").isArray());
        result.andExpect(jsonPath("$.content").isEmpty());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));

        result.andExpect(jsonPath("$.page.size").value(10));
        result.andExpect(jsonPath("$.page.totalElements").value(0));
        result.andExpect(jsonPath("$.page.totalPages").value(0));
        result.andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void findAll_withPlatforms_returnsPlatformDtosHateoasResponse() throws Exception {
        // Arrange
        PlatformDto platformDto1 = new PlatformDto();
        platformDto1.setId(1L);
        platformDto1.setName("platform-name-one");
        platformDto1.setDescription("This sure is the description");
        platformDto1.setReleaseDate(LocalDate.now());
        platformDto1.setVersion(2L);

        PlatformDto platformDto2 = new PlatformDto();
        platformDto2.setId(2L);
        platformDto2.setName("platform-name-two");
        platformDto2.setDescription("This sure is the description");
        platformDto2.setReleaseDate(LocalDate.now());
        platformDto2.setVersion(2L);

        Mockito.when(platformService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(platformDto1, platformDto2));

        // Act
        ResultActions result = mockMvc.perform(get("/v1/platforms")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));

        result.andExpect(jsonPath("$.content").isArray());
        result.andExpect(jsonPath("$.content").isNotEmpty());

        result.andExpect(jsonPath("$.content[0].id").value(platformDto1.getId()));
        result.andExpect(jsonPath("$.content[0].name").value(platformDto1.getName()));
        result.andExpect(jsonPath("$.content[0].description").value(platformDto1.getDescription()));
        result.andExpect(jsonPath("$.content[0].releaseDate").exists());
        result.andExpect(jsonPath("$.content[0].version").value(platformDto1.getVersion()));

        result.andExpect(jsonPath("$.content[0].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[0].links[1].rel").value("games"));

        result.andExpect(jsonPath("$.content[1].id").value(platformDto2.getId()));
        result.andExpect(jsonPath("$.content[1].name").value(platformDto2.getName()));
        result.andExpect(jsonPath("$.content[1].description").value(platformDto2.getDescription()));
        result.andExpect(jsonPath("$.content[1].releaseDate").exists());
        result.andExpect(jsonPath("$.content[1].version").value(platformDto2.getVersion()));

        result.andExpect(jsonPath("$.content[1].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[1].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[1].links[1].rel").value("games"));

        result.andExpect(jsonPath("$.page.size").value(10));
        result.andExpect(jsonPath("$.page.totalElements").value(2));
        result.andExpect(jsonPath("$.page.totalPages").value(1));
        result.andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void update_withNullPlatformDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(put("/v1/platforms")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(content().contentType(MediaTypes.HAL_JSON));
        result.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.debugMessage").isEmpty());
        result.andExpect(jsonPath("$.subErrors").isEmpty());
    }

    @Test
    public void update_withInvalidPlatformDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(put("/v1/platforms")
                .content(new ObjectMapper().writeValueAsString(new PlatformDto()))
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.debugMessage").isEmpty());
        result.andExpect(jsonPath("$.subErrors").isNotEmpty());
        result.andExpect(jsonPath("$.subErrors[0].@type").value("ApiValidationError"));
    }

    @Test
    public void update_withValidPlatformDto_returnsPlatformDtoHateoasResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setName("platform-name");
        platformDto.setDescription("This sure is a description of a platform.");
        platformDto.setReleaseDate(LocalDate.now());
        platformDto.setVersion(0L);

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jackson2HalModule())
                .registerModule(new JSR353Module());

        Mockito.when(platformService.update(ArgumentMatchers.any()))
                .thenReturn(platformDto);

        // Act
        ResultActions result = mockMvc.perform(put("/v1/platforms")
                .content(objectMapper.writeValueAsString(platformDto))
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.releaseDate").exists());
        result.andExpect(jsonPath("$.version").exists());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));
        result.andExpect(jsonPath("$.links[1].rel").value("games"));
    }
}
