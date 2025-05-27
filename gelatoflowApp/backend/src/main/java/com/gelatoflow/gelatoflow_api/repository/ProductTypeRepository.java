package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.ProductTypeData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductTypeData, Long> {

}
