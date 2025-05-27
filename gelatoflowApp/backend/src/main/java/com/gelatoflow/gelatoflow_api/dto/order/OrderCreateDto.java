package com.gelatoflow.gelatoflow_api.dto.order;

import com.gelatoflow.gelatoflow_api.entity.OrdersData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreateDto {
    private Long id;
    private String title;
    private String description;
    private Long statusId;
    private Long priorityId;
    private Long shopId;

    private List<OrderVariantDto> variants;

    public static OrdersData toEntity(OrderCreateDto order) {
        OrdersData ordersData = new OrdersData();
        ordersData.setId(order.getId());
        ordersData.setTitle(order.getTitle());
        ordersData.setDescription(order.getDescription());
        ordersData.setStatusId(order.getStatusId());
        ordersData.setPriorityId(order.getPriorityId());
        ordersData.setShopId(order.getShopId());
        return ordersData;
    }

}
