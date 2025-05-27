package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ICShopDto {

    private Long id;
    private String name;

    private ICShopDto (Long id, String name){
        this.id = id;
        this.name = name;
    }

    public static ICShopDto toDto(ICShopData icShopData) {
        return new ICShopDto(
                icShopData.getId(),
                icShopData.getName()
        );
    }
}
