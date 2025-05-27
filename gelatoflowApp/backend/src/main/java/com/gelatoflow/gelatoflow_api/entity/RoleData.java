package com.gelatoflow.gelatoflow_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "GF_ROLES")
public class RoleData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_ROL_ID_SEQ")
    @SequenceGenerator(name = "PK_ROL_ID_SEQ", sequenceName = "PK_ROL_ID_SEQ", initialValue = 50)
    @Column(name = "ROL_ID")
    private Long id;

    @Column(name = "ROL_NAME", nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<UserData> users;
}