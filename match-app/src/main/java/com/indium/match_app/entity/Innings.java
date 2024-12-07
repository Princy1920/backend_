package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "innings")
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "team")
    private String team;

    @Column(name = "innings_number", nullable = false)
    private Integer inningsNumber;

    @ManyToOne
    @JoinColumn(name = "info_id", nullable = false)
    private Info info;

}
