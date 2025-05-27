package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import com.gelatoflow.gelatoflow_api.entity.OrderPriorityData;
import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ProductRepository extends JpaRepository<ProductsData, Long> {

    @Query("SELECT pr FROM ProductsData pr WHERE " + "LOWER(pr.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Set<ProductsData> findByName(@Param(value = "name") String name);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM ProductsData p " +
            "WHERE p.name = :name AND p.typeId = :typeId")
    boolean existsByNameAndType(@Param("name") String name, @Param("typeId") Long typeId);
}
