package com.gelatoflow.gelatoflow_api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "GF_USERS_SHOPS")
public class UsersShopsData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_USH_ID_SEQ")
    @SequenceGenerator(name = "PK_USH_ID_SEQ", sequenceName = "PK_USH_ID_SEQ", initialValue = 50)
    @Column(name = "USH_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USH_USR_ID", nullable = false)
    private UserData user;

    @ManyToOne
    @JoinColumn(name = "USH_ICS_ID",nullable = false)
    private ICShopData shop;
}
