package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ICShopCreateDto {

    private Long id;
    private String name;
    private String location;
    private Long ownerId;

    public ICShopCreateDto(Long id, String name, String location, Long ownerId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.ownerId = ownerId;
    }

    public static ICShopCreateDto toDto(ICShopData icShopData) {
        return new ICShopCreateDto(
                icShopData.getId(),
                icShopData.getName(),
                icShopData.getLocation(),
                icShopData.getOwnerId()
        );
    }

    public static ICShopData toEntity(ICShopCreateDto icShopCreateDto) {
        return new ICShopData(
                icShopCreateDto.getId(),
                icShopCreateDto.getName(),
                icShopCreateDto.getLocation(),
                icShopCreateDto.getOwnerId()
        );
    }
}
