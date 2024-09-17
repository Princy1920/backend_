package com.indium.match_app.service;

import com.indium.match_app.repository.DeliveriesRepository;
import com.indium.match_app.repository.InningsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MatchServiceTest {

    @Mock
    private DeliveriesRepository deliveriesRepository;

    @Mock
    private InningsRepository inningsRepository;

    private CacheManager cacheManager;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheManager = new ConcurrentMapCacheManager("cumulativeScore", "matchesByPlayerName", "scoresByDate", "topBatsmen");
    }

    @Test
    void testGetCumulativeScoreByPlayerName_withCaching() {
        // Mock repository response
        when(deliveriesRepository.findCumulativeScoreByPlayerName("Test Player"))
                .thenReturn(100);

        // Call the service method
        Integer cumulativeScore = matchService.getCumulativeScoreByPlayerName("Test Player");

        // Verify the results
        assertNotNull(cumulativeScore);
        assertEquals(100, cumulativeScore);
    }

    @Test
    void testGetMatchNumbersAndPlayerNames_withCaching() {
        // Prepare mock result
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, "Test Player"});

        // Mock repository response
        when(inningsRepository.findMatchNumberAndPlayerNameByPlayerName("Test Player"))
                .thenReturn(mockResults);

        // Call the service method
        List<Map<String, Object>> result = matchService.getMatchNumbersAndPlayerNames("Test Player");

        // Verify the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).get("matchNumber"));
        assertEquals("Test Player", result.get(0).get("playerName"));
    }

    @Test
    void testGetScoresByDate_withCaching() {
        // Prepare mock result
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, 200L});

        // Mock repository response
        when(deliveriesRepository.findScoresByMatchDate(any(LocalDate.class)))
                .thenReturn(mockResults);

        // Call the service method
        Map<Integer, Long> result = matchService.getScoresByDate(LocalDate.now());

        // Verify the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(200L, result.get(1));
    }

    @Test
    void testGetTopBatsmen_withCaching() {
        // Prepare mock Page<String>
        List<String> batsmenList = Arrays.asList("Player1", "Player2");
        Page<String> mockPage = new PageImpl<>(batsmenList);

        // Mock repository response
        when(deliveriesRepository.findTopBatsmen(any(PageRequest.class)))
                .thenReturn(mockPage);

        // Call the service method
        Page<String> result = matchService.getTopBatsmen(0, 10);

        // Verify the results
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Player1", result.getContent().get(0));
    }
}
