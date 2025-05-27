package com.gelatoflow.gelatoflow_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GF_PRODUCTS_TYPE")
public class ProductTypeData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_PPT_ID_SEQ")
    @SequenceGenerator(name = "PK_PPT_ID_SEQ", sequenceName = "PK_PPT_ID_SEQ", initialValue = 50)
    @Column(name = "PPT_ID", length = 10, nullable = false)
    private Long id;

    @Column(name = "PPT_TYPE_NAME", length = 50, nullable = false)
    private String name;


    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductsData> products;
}
