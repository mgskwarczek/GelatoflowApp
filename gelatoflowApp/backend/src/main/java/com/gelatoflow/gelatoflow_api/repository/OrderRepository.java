package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import com.gelatoflow.gelatoflow_api.entity.OrderPriorityData;
import com.gelatoflow.gelatoflow_api.entity.OrdersData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface OrderRepository extends JpaRepository<OrdersData, Long> {
    @Query("SELECT or FROM OrdersData or WHERE " + "LOWER(or.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Set<OrdersData> findByTitle(@Param(value = "title") String title);
}
