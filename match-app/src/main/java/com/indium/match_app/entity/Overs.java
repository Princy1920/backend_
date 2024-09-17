package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "overs")
public class Overs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "over_number")
    private Integer overNumber;

    @ManyToOne
    @JoinColumn(name = "info_id", nullable = false)
    private Info info;
}
