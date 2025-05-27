package com.gelatoflow.gelatoflow_api.dto.product;

import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String typeName;

    public ProductDto(ProductsData product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.typeName = product.getType() != null ? product.getType().getName() : null;
    }

}

