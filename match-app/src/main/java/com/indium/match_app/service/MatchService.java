package com.indium.match_app.service;

import com.indium.match_app.entity.Info;
import com.indium.match_app.repository.DeliveriesRepository;
import com.indium.match_app.repository.InfoRepository;
import com.indium.match_app.repository.InningsRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatchService {

    private final InningsRepository inningsRepository;
    private final InfoRepository infoRepository;
    private final DeliveriesRepository deliveriesRepository;

    // Constructor for dependency injection
    public MatchService(InningsRepository inningsRepository, InfoRepository infoRepository, DeliveriesRepository deliveriesRepository) {
        this.inningsRepository = inningsRepository;
        this.infoRepository = infoRepository;
        this.deliveriesRepository = deliveriesRepository;
    }

    //cumulative score by player name
    @Cacheable(value = "cumulativeScore", key = "#playerName")
    public Integer getCumulativeScoreByPlayerName(String playerName) {
        return deliveriesRepository.findCumulativeScoreByPlayerName(playerName);
    }

    // get the number of matches played by the player
    @Cacheable(value = "matchesByPlayerName", key = "#playerName")
    public List<Map<String, Object>> getMatchNumbersAndPlayerNames(String playerName) {
        List<Object[]> results = inningsRepository.findMatchNumberAndPlayerNameByPlayerName(playerName);

        List<Map<String, Object>> processedResults = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("matchNumber", result[0]);
            resultMap.put("playerName", result[1]);
            processedResults.add(resultMap);
        }

        return processedResults;
    }

    // get the scores by date
    @Cacheable(value = "scoresByDate", key = "#matchDate")
    public Map<Integer, Long> getScoresByDate(LocalDate matchDate) {
        List<Object[]> results = deliveriesRepository.findScoresByMatchDate(matchDate);
        Map<Integer, Long> matchScores = new HashMap<>();

        for (Object[] result : results) {
            Integer matchNumber = (Integer) result[0];
            Long totalRuns = (Long) result[1];
            matchScores.put(matchNumber, totalRuns);
        }

        return matchScores;
    }

    //get top batsman
    @Cacheable(value = "topBatsmen", key = "#page + '-' + #size")
    public Page<String> getTopBatsmen(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return deliveriesRepository.findTopBatsmen(pageRequest);
    }

    @CacheEvict(value = {"matchesByPlayerName", "cumulativeScore", "scoresByDate", "topBatsmen"}, allEntries = true)
    public void clearCache() {
        // Cache evicted
    }
}

