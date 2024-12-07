package com.indium.match_app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.match_app.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MatchControllerTest {

    @Mock
    private MatchService matchService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MatchController matchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMatchNumbersAndPlayerNames() throws JsonProcessingException {
        String playerName = "Virat Kohli";
        List<Map<String, Object>> expectedMatches = Collections.singletonList(Map.of("matchNumber", 1));

        when(matchService.getMatchNumbersAndPlayerNames(playerName)).thenReturn(expectedMatches);
        when(objectMapper.writeValueAsString(any())).thenReturn("SerializedData");

        ResponseEntity<List<Map<String, Object>>> response = matchController.getMatchNumbersAndPlayerNames(playerName);

        assertEquals(expectedMatches, response.getBody());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testGetCumulativeScoreByPlayer() throws JsonProcessingException {
        String playerName = "Virat Kohli";
        int expectedScore = 5000;

        when(matchService.getCumulativeScoreByPlayerName(playerName)).thenReturn(expectedScore);
        when(objectMapper.writeValueAsString(any())).thenReturn("SerializedData");

        ResponseEntity<Integer> response = matchController.getCumulativeScoreByPlayer(playerName);

        assertEquals(expectedScore, response.getBody());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testGetScoresByDate() throws JsonProcessingException {
        LocalDate matchDate = LocalDate.now();
        Map<Integer, Long> expectedScores = Map.of(1, 300L);

        when(matchService.getScoresByDate(matchDate)).thenReturn(expectedScores);
        when(objectMapper.writeValueAsString(any())).thenReturn("SerializedData");

        ResponseEntity<Map<Integer, Long>> response = matchController.getScoresByDate(matchDate);

        assertEquals(expectedScores, response.getBody());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testGetTopBatsmen() throws JsonProcessingException {
        int page = 0;
        int size = 10;
        List<String> batsmen = List.of("Sachin Tendulkar", "Brian Lara");
        Page<String> expectedBatsmen = new PageImpl<>(batsmen);

        when(matchService.getTopBatsmen(page, size)).thenReturn(expectedBatsmen);
        when(objectMapper.writeValueAsString(any())).thenReturn("SerializedData");

        ResponseEntity<Page<String>> response = matchController.getTopBatsmen(page, size);

        assertEquals(expectedBatsmen, response.getBody());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testKafkaMessageSendingError() throws JsonProcessingException {
        String playerName = "Virat Kohli";
        List<Map<String, Object>> expectedMatches = Collections.singletonList(Map.of("matchNumber", 1));

        when(matchService.getMatchNumbersAndPlayerNames(playerName)).thenReturn(expectedMatches);
        doThrow(new JsonProcessingException("Serialization error") {}).when(objectMapper).writeValueAsString(any());

        ResponseEntity<List<Map<String, Object>>> response = matchController.getMatchNumbersAndPlayerNames(playerName);

        assertEquals(expectedMatches, response.getBody());
        verify(kafkaTemplate).send(anyString(), contains("Error serializing data"));
    }
}
