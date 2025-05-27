package com.gelatoflow.gelatoflow_api.dto.user;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ICShopDetailsDto {


    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime deletionDate;

    public ICShopDetailsDto(Long id, String name, LocalDateTime creationDate, LocalDateTime modificationDate, LocalDateTime deletionDate) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.deletionDate = deletionDate;
    }

    public static ICShopDetailsDto toDto(ICShopData icShopData) {
        return new ICShopDetailsDto(
                icShopData.getId(),
                icShopData.getName(),
                icShopData.getCreationDate(),
                icShopData.getModificationDate(),
                icShopData.getDeletionDate()
        );
    }
}
