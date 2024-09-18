package com.indium.match_app.controller;

import com.indium.match_app.service.JsonDataUploadService;
import com.indium.match_app.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class JsonDataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JsonDataUploadService jsonDataUploadService;

    @MockBean
    private MatchService matchService;

    @BeforeEach
    public void setUp() {
        // Any necessary setup before each test
    }

    @Test
    public void testUploadJsonData_ValidJson() throws Exception {
        // Arrange
        String validJsonContent = "{ \"meta\": { \"data_version\": \"1.0\", \"created\": \"2023-09-15\", \"revision\": 1 }, \"data\": [] }";

        // Mock the service behavior
        Mockito.doNothing().when(jsonDataUploadService).uploadJsonData(Mockito.anyString());
        Mockito.doNothing().when(matchService).clearCache();

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonContent))
                .andExpect(status().isOk()) // Expecting HTTP 200
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("JSON data uploaded and processed successfully");
    }

    @Test
    public void testUploadJsonData_EmptyJson() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")) // Empty content
                .andExpect(status().isBadRequest()) // Expecting HTTP 400
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("JSON content cannot be null or empty");
    }

    @Test
    public void testUploadJsonData_IOException() throws Exception {
        // Arrange
        String validJsonContent = "{ \"meta\": { \"data_version\": \"1.0\", \"created\": \"2023-09-15\", \"revision\": 1 }, \"data\": [] }";

        // Mock the service to throw IOException
        Mockito.doThrow(new IOException("Simulated IO Exception")).when(jsonDataUploadService).uploadJsonData(Mockito.anyString());

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonContent))
                .andExpect(status().isInternalServerError()) // Expecting HTTP 500
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("Failed to process the JSON data: Simulated IO Exception");
    }

    @Test
    public void testUploadJsonData_UnexpectedException() throws Exception {
        // Arrange
        String validJsonContent = "{ \"meta\": { \"data_version\": \"1.0\", \"created\": \"2023-09-15\", \"revision\": 1 }, \"data\": [] }";

        // Mock the service to throw a generic RuntimeException
        Mockito.doThrow(new RuntimeException("Unexpected error occurred")).when(jsonDataUploadService).uploadJsonData(Mockito.anyString());

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonContent))
                .andExpect(status().isInternalServerError()) // Expecting HTTP 500
                .andReturn();

        // Verify response content
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("An unexpected error occurred: Unexpected error occurred");
    }
}
