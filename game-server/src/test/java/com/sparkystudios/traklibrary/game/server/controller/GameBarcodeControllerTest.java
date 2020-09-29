package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.BarcodeType;
import com.sparkystudios.traklibrary.game.server.assembler.GameBarcodeRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.service.GameBarcodeService;
import com.sparkystudios.traklibrary.game.service.dto.GameBarcodeDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@Import({GameBarcodeController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GameBarcodeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class GameBarcodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameBarcodeService gameBarcodeService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GameBarcodeRepresentationModelAssembler gameBarcodeRepresentationModelAssembler() {
            return new GameBarcodeRepresentationModelAssembler();
        }
    }

    @Test
    void findByBarcode_withValidBarcode_return200AndValidResponse() throws Exception {
        // Arrange
        GameBarcodeDto gameBarcodeDto = new GameBarcodeDto();
        gameBarcodeDto.setId(1L);
        gameBarcodeDto.setGameId(2L);
        gameBarcodeDto.setPlatformId(3L);
        gameBarcodeDto.setBarcode("barcode");
        gameBarcodeDto.setBarcodeType(BarcodeType.UPC_A);
        gameBarcodeDto.setCreatedAt(LocalDateTime.now());
        gameBarcodeDto.setUpdatedAt(LocalDateTime.now());
        gameBarcodeDto.setVersion(1L);

        Mockito.when(gameBarcodeService.findByBarcode(ArgumentMatchers.anyString()))
                .thenReturn(gameBarcodeDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/barcodes/barcode")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int)gameBarcodeDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameId", Matchers.is((int)gameBarcodeDto.getGameId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.platformId", Matchers.is((int)gameBarcodeDto.getPlatformId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.barcode", Matchers.is(gameBarcodeDto.getBarcode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.barcodeType", Matchers.is(gameBarcodeDto.getBarcodeType().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.game").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.gameDetails").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.platform").exists());
    }
}
