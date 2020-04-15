package com.sparky.trak.game.server.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import com.sparky.trak.game.domain.AgeRating;
import com.sparky.trak.game.server.assembler.ConsoleRepresentationModelAssembler;
import com.sparky.trak.game.server.assembler.GameRepresentationModelAssembler;
import com.sparky.trak.game.server.exception.GlobalExceptionHandler;
import com.sparky.trak.game.service.ConsoleService;
import com.sparky.trak.game.service.GameService;
import com.sparky.trak.game.service.dto.ConsoleDto;
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
public class ConsoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConsoleService consoleService;

    @Mock
    private GameService gameService;

    @Spy
    private ConsoleRepresentationModelAssembler consoleRepresentationModelAssembler;

    @Spy
    private GameRepresentationModelAssembler gameRepresentationModelAssembler;

    @InjectMocks
    private ConsoleController consoleController;

    @BeforeAll
    public void beforeAll() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(consoleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new SpecificationArgumentResolver())
                .build();
    }

    @AfterEach
    public void afterEach() {
        Mockito.reset(consoleService);
    }

    @Test
    public void save_withNullConsoleDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/v1/consoles")
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
    public void save_withInvalidConsoleDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/v1/consoles")
                .content(new ObjectMapper().writeValueAsString(new ConsoleDto()))
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
    public void save_withValidConsoleDto_returnsConsoleDtoHateoasResponse() throws Exception {
        // Arrange
        ConsoleDto consoleDto = new ConsoleDto();
        consoleDto.setName("console-name");
        consoleDto.setDescription("This sure is a description of a console.");
        consoleDto.setReleaseDate(LocalDate.now());
        consoleDto.setVersion(0L);

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jackson2HalModule())
                .registerModule(new JSR353Module());

        Mockito.when(consoleService.save(ArgumentMatchers.any()))
                .thenReturn(consoleDto);

        // Act
        ResultActions result = mockMvc.perform(post("/v1/consoles")
                .content(objectMapper.writeValueAsString(consoleDto))
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
        Mockito.when(consoleService.findById(ArgumentMatchers.anyLong()))
                .thenThrow(new EntityNotFoundException("Entity sure is missing."));

        // Act
        ResultActions result = mockMvc.perform(get("/v1/consoles/1")
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
    public void findById_withValidId_returnsConsoleDtoHateoasResponse() throws Exception {
        // Arrange
        ConsoleDto consoleDto = new ConsoleDto();
        consoleDto.setId(1L);
        consoleDto.setName("console-name");
        consoleDto.setDescription("The description of the console.");
        consoleDto.setReleaseDate(LocalDate.now());
        consoleDto.setVersion(2L);

        Mockito.when(consoleService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(consoleDto);

        // Act
        ResultActions result = mockMvc.perform(get("/v1/consoles/1")
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
    public void findGamesByConsoleId_withNoGames_returnsEmptyHateoasResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findGamesByConsoleId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions result = mockMvc.perform(get("/v1/consoles/1/games")
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
    public void findGamesByConsoleId_withGames_returnsGameDtoHateoasResponse() throws Exception {
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

        Mockito.when(gameService.findGamesByConsoleId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        // Act
        ResultActions result = mockMvc.perform(get("/v1/consoles/1/games")
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
        result.andExpect(jsonPath("$.content[0].links[1].rel").value("consoles"));
        result.andExpect(jsonPath("$.content[0].links[2].rel").value("genres"));

        result.andExpect(jsonPath("$.content[1].id").value(gameDto2.getId()));
        result.andExpect(jsonPath("$.content[1].title").value(gameDto2.getTitle()));
        result.andExpect(jsonPath("$.content[1].description").value(gameDto2.getDescription()));
        result.andExpect(jsonPath("$.content[1].releaseDate").exists());
        result.andExpect(jsonPath("$.content[1].ageRating").value(gameDto2.getAgeRating().name()));
        result.andExpect(jsonPath("$.content[1].version").value(gameDto2.getVersion()));

        result.andExpect(jsonPath("$.content[1].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[1].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[1].links[1].rel").value("consoles"));
        result.andExpect(jsonPath("$.content[1].links[2].rel").value("genres"));

        result.andExpect(jsonPath("$.page.size").value(10));
        result.andExpect(jsonPath("$.page.totalElements").value(2));
        result.andExpect(jsonPath("$.page.totalPages").value(1));
        result.andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void findAll_withNoConsoles_returnsEmptyHateoasResponse() throws Exception {
        // Arrange
        Mockito.when(consoleService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions result = mockMvc.perform(get("/v1/consoles")
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
    public void findAll_withConsoles_returnsConsoleDtosHateoasResponse() throws Exception {
        // Arrange
        ConsoleDto consoleDto1 = new ConsoleDto();
        consoleDto1.setId(1L);
        consoleDto1.setName("console-name-one");
        consoleDto1.setDescription("This sure is the description");
        consoleDto1.setReleaseDate(LocalDate.now());
        consoleDto1.setVersion(2L);

        ConsoleDto consoleDto2 = new ConsoleDto();
        consoleDto2.setId(2L);
        consoleDto2.setName("console-name-two");
        consoleDto2.setDescription("This sure is the description");
        consoleDto2.setReleaseDate(LocalDate.now());
        consoleDto2.setVersion(2L);

        Mockito.when(consoleService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(consoleDto1, consoleDto2));

        // Act
        ResultActions result = mockMvc.perform(get("/v1/consoles")
                .contentType(MediaTypes.HAL_JSON_VALUE));

        // Assert
        result.andExpect(status().isOk());

        result.andExpect(jsonPath("$.links").isNotEmpty());
        result.andExpect(jsonPath("$.links[0].rel").value("self"));

        result.andExpect(jsonPath("$.content").isArray());
        result.andExpect(jsonPath("$.content").isNotEmpty());

        result.andExpect(jsonPath("$.content[0].id").value(consoleDto1.getId()));
        result.andExpect(jsonPath("$.content[0].name").value(consoleDto1.getName()));
        result.andExpect(jsonPath("$.content[0].description").value(consoleDto1.getDescription()));
        result.andExpect(jsonPath("$.content[0].releaseDate").exists());
        result.andExpect(jsonPath("$.content[0].version").value(consoleDto1.getVersion()));

        result.andExpect(jsonPath("$.content[0].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[0].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[0].links[1].rel").value("games"));

        result.andExpect(jsonPath("$.content[1].id").value(consoleDto2.getId()));
        result.andExpect(jsonPath("$.content[1].name").value(consoleDto2.getName()));
        result.andExpect(jsonPath("$.content[1].description").value(consoleDto2.getDescription()));
        result.andExpect(jsonPath("$.content[1].releaseDate").exists());
        result.andExpect(jsonPath("$.content[1].version").value(consoleDto2.getVersion()));

        result.andExpect(jsonPath("$.content[1].links").isNotEmpty());
        result.andExpect(jsonPath("$.content[1].links[0].rel").value("self"));
        result.andExpect(jsonPath("$.content[1].links[1].rel").value("games"));

        result.andExpect(jsonPath("$.page.size").value(10));
        result.andExpect(jsonPath("$.page.totalElements").value(2));
        result.andExpect(jsonPath("$.page.totalPages").value(1));
        result.andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    public void update_withNullConsoleDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(put("/v1/consoles")
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
    public void update_withInvalidConsoleDto_returns400AndApiError() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(put("/v1/consoles")
                .content(new ObjectMapper().writeValueAsString(new ConsoleDto()))
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
    public void update_withValidConsoleDto_returnsConsoleDtoHateoasResponse() throws Exception {
        // Arrange
        ConsoleDto consoleDto = new ConsoleDto();
        consoleDto.setName("console-name");
        consoleDto.setDescription("This sure is a description of a console.");
        consoleDto.setReleaseDate(LocalDate.now());
        consoleDto.setVersion(0L);

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jackson2HalModule())
                .registerModule(new JSR353Module());

        Mockito.when(consoleService.update(ArgumentMatchers.any()))
                .thenReturn(consoleDto);

        // Act
        ResultActions result = mockMvc.perform(put("/v1/consoles")
                .content(objectMapper.writeValueAsString(consoleDto))
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
