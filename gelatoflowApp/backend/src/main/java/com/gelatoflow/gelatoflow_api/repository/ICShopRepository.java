package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.ICShopData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ICShopRepository extends JpaRepository<ICShopData, Long> {

    @Query("SELECT ic FROM ICShopData ic WHERE ic.deletionDate IS NULL")
    List<ICShopData> findAllActiveShops();

    @Query("SELECT ic FROM ICShopData ic WHERE " + "LOWER(ic.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Set<ICShopData> findByName(@Param(value = "name") String name);
}
