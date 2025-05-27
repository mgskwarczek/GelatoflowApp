package com.gelatoflow.gelatoflow_api.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="GF_PRODUCTS_VARIANTS")
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_PRV_ID_SEQ")
    @SequenceGenerator(name="PK_PRV_ID_SEQ", sequenceName = "PK_PRV_ID_SEQ", initialValue = 50)

    @Column(name="PRV_ID")
    private Long id;

    @Column(name="PRV_NAME")
    private String variantName;

    @Column(name="PRV_QUANTITY")
    private Long quantity;

    @Column(name="PRV_CRE_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name="PRV_MOD_DATE")
    private LocalDateTime modificationDate;

    @Column(name="PRV_DEL_DATE")
    private LocalDateTime deletionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PRV_PRD_ID", referencedColumnName = "PRD_ID", nullable = false)
    private ProductsData product;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdersProductsData> orderProducts;


    public ProductVariantData(Long id, String variantName, Long quantity) {
        this.id = id;
        this.variantName = variantName;
        this.quantity = quantity;
    }
}
