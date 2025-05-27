package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ICShopUpdateDto {
    private Long id;
    private String name;

    private ICShopUpdateDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ICShopUpdateDto toDto(ICShopData icShopData) {
        return new ICShopUpdateDto(
                icShopData.getId(),
                icShopData.getName()
        );
    }
}
