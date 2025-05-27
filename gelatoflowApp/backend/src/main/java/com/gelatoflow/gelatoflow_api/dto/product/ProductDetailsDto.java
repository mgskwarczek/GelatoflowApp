package com.gelatoflow.gelatoflow_api.dto.product;

import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDetailsDto {
    private Long id;
    private String name;
    private String description;
    private String typeName;


    public ProductDetailsDto(Long id, String name, String description, String typeName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.typeName = typeName;
    }

    public static ProductDetailsDto toDto(ProductsData product) {
        return new ProductDetailsDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getType() != null ? product.getType().getName() : null
        );
    }
}
