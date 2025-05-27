package com.gelatoflow.gelatoflow_api.dto.product;

import com.gelatoflow.gelatoflow_api.entity.ProductTypeData;
import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateDto {
    private Long id;
    private String name;
    private String description;
    private Long typeId;

    public static ProductsData toEntity(ProductCreateDto productCreateDto) {
        return new ProductsData(
                productCreateDto.getId(),
                productCreateDto.getName(),
                productCreateDto.getDescription(),
                productCreateDto.getTypeId()
        );
    }


}
