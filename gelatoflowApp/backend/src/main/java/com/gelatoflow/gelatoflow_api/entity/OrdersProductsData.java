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
@Table(name = "GF_ORDERS_PRODUCTS")
public class OrdersProductsData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_OPD_ID_SEQ")
    @SequenceGenerator(name = "PK_OPD_ID_SEQ", sequenceName = "PK_OPD_ID_SEQ", initialValue = 50)
    @Column(name = "OPD_ID", length = 10, nullable = false)
    private Long id;

    @Column(name="OPD_QUANTITY", nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPD_ORD_ID",referencedColumnName = "ORD_ID", nullable = false)
    private OrdersData order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPD_PRD_ID", referencedColumnName = "PRD_ID", nullable = false)
    private ProductsData product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPD_PRV_ID",referencedColumnName = "PRV_ID", nullable = false)
    private ProductVariantData productVariant;


}
