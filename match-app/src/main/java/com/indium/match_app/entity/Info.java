package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "info")
public class Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "balls_per_over")
    private Integer ballsPerOver;

    @Column(name = "city")
    private String city;

    @Column(name = "match_number")
    private Integer matchNumber;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "match_type")
    private String matchType;

    @Column(name = "outcome_by_runs")
    private Integer outcomeByRuns;

    @Column(name = "outcome_winner")
    private String outcomeWinner;

    @Column(name = "overs")
    private Integer overs;

    @Column(name = "player_of_match")
    private String playerOfMatch;

    @Column(name = "season")
    private String season;

    @Column(name = "team_type")
    private String teamType;

    @Column(name = "toss_decision")
    private String tossDecision;

    @Column(name = "toss_winner")
    private String tossWinner;

    @Column(name = "venue")
    private String venue;

    @Column(name = "match_date")
    private LocalDate matchDate;

    // One Info can have many innings
    @OneToMany(mappedBy = "info", cascade = CascadeType.ALL)
    private List<Innings> inningsList;

    @OneToMany(mappedBy = "info", cascade = CascadeType.ALL)
    private List<Overs> oversList;

    // One Info can have many officials
    @OneToMany(mappedBy = "info", cascade = CascadeType.ALL)
    private List<Officials> officialsList;
}
