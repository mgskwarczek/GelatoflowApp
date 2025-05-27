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
@Table(name = "GF_ORDERS_PRIORITY")
public class OrderPriorityData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_OPR_ID_SEQ")
    @SequenceGenerator(name = "PK_OPR_ID_SEQ", sequenceName = "PK_OPR_ID_SEQ", initialValue = 50)
    @Column(name = "OPR_ID", length = 10, nullable = false)
    private Long id;

    @Column(name = "OPR_PRIORITY_NAME", length = 50, nullable = false)
    private String priorityName;
}
