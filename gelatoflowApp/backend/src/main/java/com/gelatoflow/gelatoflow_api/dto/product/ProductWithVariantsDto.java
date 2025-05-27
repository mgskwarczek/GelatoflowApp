package com.gelatoflow.gelatoflow_api.dto.product;

import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductWithVariantsDto {
    private Long id;
    private String name;
    private String typeName;
    private List<VariantDto> variants;

    public ProductWithVariantsDto(ProductsData product) {
        this.id = product.getId();
        this.name = product.getName();
        this.typeName = product.getType() != null ? product.getType().getName() : null;
        this.variants = product.getVariants().stream()
                .map(VariantDto::new)
                .toList();
    }

}
