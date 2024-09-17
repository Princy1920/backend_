package com.indium.match_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "officials")
public class Officials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role")
    private String role;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "info_id", nullable = false)
    private Info info;
}
