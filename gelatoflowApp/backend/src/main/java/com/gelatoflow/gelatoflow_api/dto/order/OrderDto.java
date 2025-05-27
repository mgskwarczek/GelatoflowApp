package com.gelatoflow.gelatoflow_api.dto.order;

import com.gelatoflow.gelatoflow_api.entity.OrdersData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private String title;
    private String description;
    private Long statusId;
    private Long priorityId;
    private List<OrderVariantDto> variants;

    public OrderDto(OrdersData orders) {
        this.id = orders.getId();
        this.title = orders.getTitle();
        this.description = orders.getDescription();
        this.statusId = orders.getStatusId();
        this.priorityId = orders.getPriorityId();
    }
}
