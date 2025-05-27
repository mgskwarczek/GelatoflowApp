package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.OrderStatusData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatusData, Long> {
}
