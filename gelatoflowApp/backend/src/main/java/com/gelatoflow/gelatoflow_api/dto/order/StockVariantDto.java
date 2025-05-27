package com.gelatoflow.gelatoflow_api.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockVariantDto {
    private Long variantId;
    private Long quantity;
}
