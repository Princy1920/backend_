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

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        // Setup any initialization logic here, if necessary
    }

    @Test
    public void testUploadJsonDataEmptyContent() throws Exception {
        // Sending an empty body with content-type as application/json
        mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")) // Sending empty JSON content
                .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
                .andExpect(content().string("JSON content cannot be null or empty")); // Validating the response message
    }

    @Test
    public void testUploadJsonDataValidContent() throws Exception {
        // Mock the service call
        Mockito.doNothing().when(jsonDataUploadService).uploadJsonData(Mockito.anyString());
        Mockito.doNothing().when(matchService).clearCache();

        String validJsonContent = "{ \"meta\": { \"data_version\": \"1.0\", \"created\": \"2023-09-15\", \"revision\": 1 }, \"data\": [] }";

        // Send a valid JSON payload
        mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonContent)) // Sending valid JSON content
                .andExpect(status().isOk()) // Expecting 200 OK
                .andExpect(content().string("JSON data uploaded and processed successfully")); // Validating the response message
    }

    @Test
    public void testUploadJsonDataIOException() throws Exception {
        // Mock the service call to throw IOException
        Mockito.doThrow(new IOException("Failed to process JSON")).when(jsonDataUploadService).uploadJsonData(Mockito.anyString());

        String validJsonContent = "{ \"meta\": { \"data_version\": \"1.0\", \"created\": \"2023-09-15\", \"revision\": 1 }, \"data\": [] }";

        // Send a valid JSON payload
        mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonContent)) // Sending valid JSON content
                .andExpect(status().isInternalServerError()) // Expecting 500 Internal Server Error
                .andExpect(content().string("Failed to process the JSON data: Failed to process JSON")); // Validating the error message
    }

    @Test
    public void testUploadJsonDataUnexpectedException() throws Exception {
        // Mock the service call to throw a generic exception
        Mockito.doThrow(new RuntimeException("Unexpected error")).when(jsonDataUploadService).uploadJsonData(Mockito.anyString());

        String validJsonContent = "{ \"meta\": { \"data_version\": \"1.0\", \"created\": \"2023-09-15\", \"revision\": 1 }, \"data\": [] }";

        // Send a valid JSON payload
        mockMvc.perform(post("/api/json-upload/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonContent)) // Sending valid JSON content
                .andExpect(status().isInternalServerError()) // Expecting 500 Internal Server Error
                .andExpect(content().string("An unexpected error occurred: Unexpected error")); // Validating the error message
    }
}
