package com.gelatoflow.gelatoflow_api.dto.product;

import com.gelatoflow.gelatoflow_api.entity.ProductVariantData;
import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VariantDto {
    private Long id;
    private String variantName;
    private Long quantity;

    public VariantDto(ProductVariantData variant) {
        this.id = variant.getId();
        this.variantName = variant.getVariantName();
        this.quantity = variant.getQuantity();
    }
}
