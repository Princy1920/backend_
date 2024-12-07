package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "teams")
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "info_id")
    private Info info;

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
