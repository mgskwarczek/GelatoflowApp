package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.ProductVariantData;
import com.gelatoflow.gelatoflow_api.entity.ProductsData;
import com.gelatoflow.gelatoflow_api.entity.RoleData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariantData, Long> {
    List<ProductVariantData> findByProductId(Long productId);
}
