package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "deliveries")
public class Deliveries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batter")
    private String batter;

    @Column(name = "bowler")
    private String bowler;

    @Column(name = "non_striker")
    private String nonStriker;

    @Column(name = "extras_legbyes")
    private Integer extrasLegbyes;

    @Column(name = "extras_wides")
    private Integer extrasWides;

    @Column(name = "extras_byes")
    private Integer extrasByes;

    @Column(name = "runs_batter")
    private Integer runsBatter;

    @Column(name = "runs_extras")
    private Integer runsExtras;

    @Column(name = "runs_total")
    private Integer runsTotal;

    @Column(name = "wicket_kind")
    private String wicketKind;

    @Column(name = "wicket_player_out")
    private String wicketPlayerOut;

    @Column(name = "wicket_fielder")
    private String wicketFielder;

    // Setter for Over
    public void setOver(Overs over) {
        this.over = over;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "over_id", nullable = false)
    private Overs over;
}
