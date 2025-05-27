package com.gelatoflow.gelatoflow_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GF_ORDERS_STATUS")
public class OrderStatusData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_OST_ID_SEQ")
    @SequenceGenerator(name = "PK_OST_ID_SEQ", sequenceName = "PK_OST_ID_SEQ", initialValue = 50)
    @Column(name = "OST_ID", length = 10, nullable = false)
    private Long id;

    @Column(name = "OST_STATUS_NAME", length = 50, nullable = false)
    private String name;
}
