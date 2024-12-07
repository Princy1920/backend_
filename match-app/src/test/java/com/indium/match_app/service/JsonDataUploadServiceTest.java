package com.indium.match_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.match_app.entity.*;
import com.indium.match_app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class JsonDataUploadServiceTest {

    @InjectMocks
    private JsonDataUploadService jsonDataUploadService;

    @Mock
    private TeamsRepository teamsRepository;

    @Mock
    private PlayersRepository playersRepository;

    @Mock
    private OfficialsRepository officialsRepository;

    @Mock
    private InningsRepository inningsRepository;

    @Mock
    private DeliveriesRepository deliveriesRepository;

    @Mock
    private InfoRepository infoRepository;

    @Mock
    private MetaRepository metaRepository;

    @Mock
    private OversRepository oversRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    void testUploadJsonData() throws IOException {
        // Sample JSON input for testing (simplified for clarity)
        String jsonData = """
                {
                  "meta": {
                    "data_version": "1.0",
                    "created": "2023-09-15",
                    "revision": 1
                  },
                  "info": {
                    "balls_per_over": 6,
                    "city": "Mumbai",
                    "event": {
                      "name": "IPL",
                      "match_number": 45
                    },
                    "teams": ["Team A", "Team B"],
                    "outcome": {
                      "by": {
                        "runs": 50
                      },
                      "winner": "Team A"
                    },
                    "player_of_match": ["Player 1"],
                    "season": "2021",
                    "team_type": "men",
                    "toss": {
                      "decision": "bat",
                      "winner": "Team A"
                    },
                    "venue": "Wankhede",
                    "overs": 20,
                    "dates": ["2021-05-25"]
                  }
                }
                """;

        // Mock the repository save methods to ensure the code works without actual DB interaction
        when(metaRepository.save(any(Meta.class))).thenReturn(new Meta());
        when(infoRepository.save(any(Info.class))).thenReturn(new Info());
        when(teamsRepository.save(any(Teams.class))).thenReturn(new Teams());
        when(playersRepository.save(any(Players.class))).thenReturn(new Players());
        when(officialsRepository.save(any(Officials.class))).thenReturn(new Officials());
        when(inningsRepository.save(any(Innings.class))).thenReturn(new Innings());
        when(oversRepository.save(any(Overs.class))).thenReturn(new Overs());
        when(deliveriesRepository.save(any(Deliveries.class))).thenReturn(new Deliveries());

        // Call the method under test
        jsonDataUploadService.uploadJsonData(jsonData);

        // Verify that the repositories' save methods were called
        verify(metaRepository, times(1)).save(any(Meta.class));
        verify(infoRepository, times(1)).save(any(Info.class));
        verify(teamsRepository, times(2)).save(any(Teams.class));  // 2 teams in the JSON
        verify(playersRepository, atLeastOnce()).save(any(Players.class));
        verify(officialsRepository, atLeastOnce()).save(any(Officials.class));
        verify(inningsRepository, atLeastOnce()).save(any(Innings.class));
        verify(oversRepository, atLeastOnce()).save(any(Overs.class));
        verify(deliveriesRepository, atLeastOnce()).save(any(Deliveries.class));
    }

    // Additional tests can be written to cover edge cases, like missing fields or invalid data

}
