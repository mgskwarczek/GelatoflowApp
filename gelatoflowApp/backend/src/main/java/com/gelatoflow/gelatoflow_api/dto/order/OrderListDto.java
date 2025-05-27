package com.gelatoflow.gelatoflow_api.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderListDto {
    private String orderTitle;
    private String shopName;
    private String shopLocation;
    private String priority;
    private String status;
    private LocalDateTime creationDate;

    public OrderListDto(String orderTitle, String shopName, String shopLocation, String priority, String status, LocalDateTime creationDate) {
        this.orderTitle = orderTitle;
        this.shopName = shopName;
        this.shopLocation = shopLocation;
        this.priority = priority;
        this.status = status;
        this.creationDate = creationDate;
    }
}
