package com.gelatoflow.gelatoflow_api.entity;

import com.gelatoflow.gelatoflow_api.dto.auditLogValues.LogValueDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "GF_PRODUCTS")
@NoArgsConstructor
@AllArgsConstructor

public class ProductsData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_PRD_ID_SEQ")
    @SequenceGenerator(name = "PK_PRD_ID_SEQ", sequenceName = "PK_PRD_ID_SEQ", initialValue = 50)
    @Column(name = "PRD_ID")
    private Long id;

    @Column(name = "PRD_NAME")
    private String name;

    @Column(name = "PRD_DESCRIPTION")
    private String description;

    @Column(name ="PRD_CRE_DATE")
    private LocalDateTime creationDate;

    @Column(name="PRD_MOD_DATE")
    private LocalDateTime modificationDate;

    @Column(name="PRD_DEL_DATE")
    private LocalDateTime deletionDate;

    //TO PO ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRD_TYPE", referencedColumnName = "PPT_ID", nullable = false, updatable = false, insertable = false)
    private ProductTypeData type;

    @Column(name="PRD_TYPE", nullable = false, updatable = false)
    private Long typeId;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private Set<ProductVariantData> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdersProductsData> orderProducts;

    public ProductsData(Long id, String name, String description, Long typeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.typeId = typeId;
    }

}
