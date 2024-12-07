package com.indium.match_app.controller;

import com.indium.match_app.service.JsonDataUploadService;
import com.indium.match_app.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@RequestMapping("/api/json-upload")
public class JsonDataController {

    private static final Logger logger = LoggerFactory.getLogger(JsonDataController.class);

    @Autowired
    private JsonDataUploadService jsonDataUploadService;

    @Autowired
    private MatchService matchService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJsonData(@RequestBody(required = false) String jsonContent) {
        logger.info("Received JSON data for processing");

        try {
            // Validate the JSON content before processing
            if (jsonContent == null || jsonContent.isEmpty()) {
                logger.warn("JSON content is empty or null");
                return ResponseEntity.badRequest().body("JSON content cannot be null or empty");
            }


            jsonDataUploadService.uploadJsonData(jsonContent);

            // Clear cache after data processing
            matchService.clearCache();

            logger.info("JSON data processed successfully");
            return ResponseEntity.ok("JSON data uploaded and processed successfully");

        } catch (IOException e) {
            logger.error("IO Exception occurred while processing the JSON data", e);
            return ResponseEntity.status(500).body("Failed to process the JSON data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while processing the JSON data", e);
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

}
