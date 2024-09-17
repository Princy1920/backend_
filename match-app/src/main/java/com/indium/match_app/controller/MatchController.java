package com.indium.match_app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.match_app.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Match Operations", description = "Endpoints for retrieving match details and player statistics")
@RestController
@RequestMapping("/matches")
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Get all matches played by a given player",
            description = "Retrieve a list of all matches played by the given player name",
            parameters = @Parameter(name = "playerName", description = "Name of the player", example = "Virat Kohli"))
    @ApiResponse(description = "List of matches played by the given player", responseCode = "200")
    @GetMapping("/player")
    public ResponseEntity<List<Map<String, Object>>> getMatchNumbersAndPlayerNames(
            @RequestParam String playerName) {
        List<Map<String, Object>> matches = matchService.getMatchNumbersAndPlayerNames(playerName);

        logAndSendToKafka("Matches for player " + playerName, matches);

        return ResponseEntity.ok(matches);
    }

    @Operation(summary = "Get cumulative score of a player",
            description = "Retrieve the cumulative score of a player across all matches",
            parameters = @Parameter(name = "playerName", description = "Name of the player", example = "Virat Kohli"))
    @ApiResponse(description = "Cumulative score of the player", responseCode = "200")
    @GetMapping("/cumulative-score")
    public ResponseEntity<Integer> getCumulativeScoreByPlayer(
            @RequestParam String playerName) {
        Integer cumulativeScore = matchService.getCumulativeScoreByPlayerName(playerName);

        logAndSendToKafka("Cumulative score for player " + playerName, cumulativeScore);

        return ResponseEntity.ok(cumulativeScore);
    }

    @GetMapping("/scores/{date}")
    public ResponseEntity<Map<Integer, Long>> getScoresByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchDate) {

        Map<Integer, Long> scores = matchService.getScoresByDate(matchDate);

        logAndSendToKafka("Scores for match date " + matchDate, scores);

        return ResponseEntity.ok(scores);
    }

    @GetMapping("/top-batsmen")
    public ResponseEntity<Page<String>> getTopBatsmen(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<String> topBatsmen = matchService.getTopBatsmen(page, size);

        logAndSendToKafka("Top batsmen for page " + page + " and size " + size, topBatsmen.getContent());

        return ResponseEntity.ok(topBatsmen);
    }

    // Helper method to log and send data to Kafka
    private void logAndSendToKafka(String logPrefix, Object data) {
        try {
            String logMessage = objectMapper.writeValueAsString(data);
            logger.info("{}: {}", logPrefix, logMessage);
            kafkaTemplate.send("Ipl-topic", logMessage);
        } catch (JsonProcessingException e) {
            String errorMessage = "Error serializing data for " + logPrefix + ": " + e.getMessage();
            logger.error(errorMessage);
            kafkaTemplate.send("Ipl-topic", errorMessage);
        }
    }
}
