package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.OrderPriorityData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPriorityRepository extends JpaRepository<OrderPriorityData, Long> {
}
