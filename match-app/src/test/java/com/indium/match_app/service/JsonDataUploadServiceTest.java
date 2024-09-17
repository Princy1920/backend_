package com.indium.match_app.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.match_app.entity.*;
import com.indium.match_app.repository.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JsonDataUploadServiceTest {

    @InjectMocks
    private JsonDataUploadService jsonDataUploadService; // Service under test

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

    // Initialize ObjectMapper directly here
    private ObjectMapper objectMapper = new ObjectMapper(); // Initialize during declaration

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // No need to initialize objectMapper again here since it's already initialized at declaration
    }

    @Test
    void testInsertTeams() throws IOException {
        // Sample JSON input with 'teams' node
        String sampleJsonData = "{\"info\": {\"teams\": [\"Team A\", \"Team B\"]}}";

        // Now the objectMapper can be used here
        JsonNode rootNode = objectMapper.readTree(sampleJsonData);
        JsonNode infoNode = rootNode.get("info");

        // Mock Info object that is passed to associate with teams
        Info mockInfo = new Info();
        mockInfo.setId(1);  // Simulate a saved info entity with an ID

        // Mock Teams entity
        Teams mockTeamA = new Teams();
        mockTeamA.setTeamName("Team A");
        mockTeamA.setInfo(mockInfo);

        Teams mockTeamB = new Teams();
        mockTeamB.setTeamName("Team B");
        mockTeamB.setInfo(mockInfo);

        // Call the actual method for inserting teams
        Map<String, Teams> teamMap = new HashMap<>();

        JsonNode teamsNode = infoNode.get("teams");
        if (teamsNode != null && teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                String teamName = teamNode.asText();
                Teams team = new Teams();
                team.setTeamName(teamName);
                team.setInfo(mockInfo);  // Associate with mock Info object
                teamsRepository.save(team);
                teamMap.put(teamName, team);
            }
        }

        // Mock behavior of the repository (you can verify actual calls)
        verify(teamsRepository, times(2)).save(any(Teams.class)); // Assert that the repository is called twice
    }




    @Test
    void testUploadJsonData_MissingMetaField() {
        // Sample JSON without the 'meta' field
        String sampleJsonData = "{\"info\": {\"city\": \"New York\", \"event\": {\"match_number\": 10, \"name\": \"Super Cup\"}, \"teams\": [\"Team A\", \"Team B\"]}}";

        // Expect an IllegalArgumentException due to missing 'meta' field
        assertThrows(IllegalArgumentException.class, () -> {
            jsonDataUploadService.uploadJsonData(sampleJsonData);
        });
    }



}

