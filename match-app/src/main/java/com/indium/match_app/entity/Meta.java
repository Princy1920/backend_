package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "meta")
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_version")
    private String dataVersion;

    @Column(name = "created")
    private String created;

    @Column(name = "revision")
    private Integer revision;
}
