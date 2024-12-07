package com.indium.match_app.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.match_app.entity.*;
import com.indium.match_app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonDataUploadService {

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private PlayersRepository playersRepository;

    @Autowired
    private OfficialsRepository officialsRepository;

    @Autowired
    private InningsRepository inningsRepository;

    @Autowired
    private DeliveriesRepository deliveriesRepository;

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private OversRepository oversRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional  // Add @Transactional here to ensure all DB operations are in one transaction
    public void uploadJsonData(String jsonData) throws IOException {
        JsonNode rootNode = objectMapper.readTree(jsonData);

        // Parse meta and info as usual
        JsonNode metaNode = rootNode.get("meta");

        if (metaNode == null) {
            throw new IllegalArgumentException("Missing required 'meta' field in the JSON data.");
        }
        Meta meta = new Meta();
        meta.setDataVersion(metaNode.get("data_version").asText());
        meta.setCreated(metaNode.get("created").asText());
        meta.setRevision(metaNode.get("revision").asInt());
        metaRepository.save(meta);

        // Insert Match Info
        JsonNode infoNode = rootNode.get("info");
        Info matchInfo = new Info();
        matchInfo.setBallsPerOver(infoNode.get("balls_per_over").asInt());
        matchInfo.setCity(infoNode.get("city").asText());
        matchInfo.setMatchNumber(infoNode.get("event").get("match_number").asInt());
        matchInfo.setEventName(infoNode.get("event").get("name").asText());
        matchInfo.setGender(infoNode.get("gender").asText());
        matchInfo.setMatchType(infoNode.get("match_type").asText());
        matchInfo.setOutcomeByRuns(infoNode.get("outcome").path("by").path("runs").asInt(0));
        matchInfo.setOutcomeWinner(infoNode.get("outcome").path("winner").asText());
        matchInfo.setOvers(infoNode.get("overs").asInt(0));
        matchInfo.setPlayerOfMatch(infoNode.path("player_of_match").get(0).asText());
        matchInfo.setSeason(infoNode.get("season").asText());
        matchInfo.setTeamType(infoNode.get("team_type").asText());
        matchInfo.setTossDecision(infoNode.path("toss").path("decision").asText());
        matchInfo.setTossWinner(infoNode.path("toss").path("winner").asText());
        matchInfo.setVenue(infoNode.get("venue").asText());

        // Parse match date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = infoNode.path("dates").get(0).asText();
        LocalDate matchDate = LocalDate.parse(dateString, formatter);
        matchInfo.setMatchDate(matchDate);

        // Save Info to repository
        Info savedInfo = infoRepository.save(matchInfo);
        System.out.println("Saved Info ID: " + savedInfo.getId());

        if (savedInfo == null || savedInfo.getId() == null) {
            throw new IllegalStateException("Failed to save Info before proceeding to insert overs.");
        }
        // Insert Teams
        JsonNode teamsNode = infoNode.get("teams");
        Map<String, Teams> teamMap = new HashMap<>();
        if (teamsNode != null && teamsNode.isArray()) {
            for (JsonNode teamNode : teamsNode) {
                String teamName = teamNode.asText();
                Teams team = new Teams();
                team.setTeamName(teamName);
                team.setInfo(matchInfo);  // Associate with info
                teamsRepository.save(team);
                teamMap.put(teamName, team);
            }
        }

        // Insert Players
        // Retrieve the "players" node inside the "info" node
        JsonNode playersNode = infoNode.get("players");

// Ensure playersNode is not null


// Ensure playersNode is not null
        if (playersNode != null) {
            // Iterate through each team inside the players node using key-value loop
            playersNode.fields().forEachRemaining(entry -> {
                String teamName = entry.getKey();  // This is the team name as a String
                JsonNode teamPlayers = entry.getValue();  // The players list for that team

                // Fetch the team entity from teamMap (of type Teams)
                Teams team = teamMap.get(teamName);

                // Check if team is null
                if (team == null) {
                    System.out.println("Team not found in teamMap: " + teamName);
                    return;  // Skip this team if not found
                }

                // If you need the actual team name later
                String actualTeamName = team.getTeamName();  // This ensures you are using a String when needed

                // Handle players associated with the team
                if (teamPlayers != null && teamPlayers.isArray()) {
                    for (JsonNode playerNode : teamPlayers) {
                        String playerName = playerNode.asText();
                        if (playerName != null && !playerName.isEmpty()) {
                            Players player = new Players();
                            player.setPlayerName(playerName);
                            player.setTeam(team);  // This should be the Teams object
                            playersRepository.save(player);
                        }
                    }
                }
            });
        }


        // Insert Officials
        JsonNode officialsNode = infoNode.get("officials");
        if (officialsNode != null) {
            insertOfficials(officialsNode.get("umpires"), matchInfo, "Umpire");
            insertOfficials(officialsNode.get("tv_umpires"), matchInfo, "TV Umpire");
            insertOfficials(officialsNode.get("match_referees"), matchInfo, "Match Referee");
        }

        // Insert Innings, Overs, and Deliveries
        insertInningsAndOvers(rootNode, matchInfo);
    }

    private void insertOfficials(JsonNode officialsNode, Info info, String role) {
        if (officialsNode != null && officialsNode.isArray()) {
            for (JsonNode officialNode : officialsNode) {
                Officials official = new Officials();
                official.setName(officialNode.asText());
                official.setRole(role);
                official.setInfo(info);
                officialsRepository.save(official);
            }
        }
    }

    protected void insertInningsAndOvers(JsonNode rootNode, Info savedInfo) {
        JsonNode inningsNode = rootNode.get("innings");
        if (inningsNode != null && inningsNode.isArray()) {
            int inningsNumber = 1; // Initialize innings number

            for (JsonNode inningNode : inningsNode) {
                String teamName = inningNode.get("team").asText();
                Innings innings = new Innings();
                innings.setTeam(teamName);
                innings.setInningsNumber(inningsNumber); // Set innings number
                innings.setInfo(savedInfo);  // Pass the saved Info entity here

                // Save Innings and capture the returned entity with the generated ID
                Innings savedInnings = inningsRepository.save(innings);

                // Log the ID to ensure it's persisted
                System.out.println("Saved Innings ID: " + savedInnings.getId());

                // Check if the ID is null after saving
                if (savedInnings.getId() == null) {
                    throw new IllegalStateException("Failed to save Innings");
                }

                // Now insert Overs with the savedInfo passed for each inning
                JsonNode oversNode = inningNode.get("overs");
                if (oversNode != null && oversNode.isArray()) {
                    insertOversAndDeliveries(oversNode, savedInfo);  // Always pass savedInfo
                }

                inningsNumber++; // Increment innings number for the next innings
            }
        }
    }

    private void insertOversAndDeliveries(JsonNode oversNode, Info savedInfo) {
        if (savedInfo == null || savedInfo.getId() == null) {
            throw new IllegalStateException("Info is not properly saved before inserting overs!");
        }

        // Iterate through each over in the overs array
        for (JsonNode overNode : oversNode) {
            int overNumber = overNode.get("over").asInt();

            Overs over = new Overs();
            over.setOverNumber(overNumber);
            over.setInfo(savedInfo);  // Link the saved Info object

            oversRepository.save(over);  // Save the over entity

            // Log for debugging purposes
            System.out.println("Saved Over ID: " + over.getId());

            // Insert Deliveries for each over
            JsonNode deliveriesNode = overNode.get("deliveries");
            if (deliveriesNode != null && deliveriesNode.isArray()) {
                for (JsonNode deliveryNode : deliveriesNode) {
                    Deliveries delivery = new Deliveries();
                    delivery.setOver(over);  // Associate the delivery with the over
                    delivery.setBatter(deliveryNode.get("batter").asText());
                    delivery.setBowler(deliveryNode.get("bowler").asText());
                    delivery.setNonStriker(deliveryNode.get("non_striker").asText());

                    // Extract runs details
                    JsonNode runsNode = deliveryNode.get("runs");
                    if (runsNode != null) {
                        delivery.setRunsBatter(runsNode.get("batter").asInt());
                        delivery.setRunsExtras(runsNode.has("extras") ? runsNode.get("extras").asInt() : 0);  // Handle runs_extras
                        delivery.setRunsTotal(runsNode.has("total") ? runsNode.get("total").asInt() : 0);  // Handle runs_total
                    }

                    // Handle extras (if any)
                    JsonNode extrasNode = deliveryNode.get("extras");
                    if (extrasNode != null) {
                        delivery.setExtrasLegbyes(extrasNode.has("legbyes") ? extrasNode.get("legbyes").asInt() : 0);
                        delivery.setExtrasWides(extrasNode.has("wides") ? extrasNode.get("wides").asInt() : 0);
                        delivery.setExtrasByes(extrasNode.has("byes") ? extrasNode.get("byes").asInt() : 0);
                    }

                    // Handle wickets (if any)
                    JsonNode wicketsNode = deliveryNode.get("wickets");
                    if (wicketsNode != null && wicketsNode.isArray()) {
                        for (JsonNode wicketNode : wicketsNode) {
                            delivery.setWicketKind(wicketNode.get("kind").asText());  // e.g., bowled, caught, etc.
                            delivery.setWicketPlayerOut(wicketNode.get("player_out").asText());

                            // Handle fielders (if any)
                            JsonNode fieldersNode = wicketNode.get("fielders");
                            if (fieldersNode != null && fieldersNode.isArray()) {
                                StringBuilder fielders = new StringBuilder();
                                for (JsonNode fielderNode : fieldersNode) {
                                    if (fielders.length() > 0) {
                                        fielders.append(", ");
                                    }
                                    fielders.append(fielderNode.asText());
                                }
                                delivery.setWicketFielder(fielders.toString());
                            }
                        }
                    }

                    // Save the delivery entity
                    deliveriesRepository.save(delivery);
                }
            }
        }
    }
}