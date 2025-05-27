package com.gelatoflow.gelatoflow_api.repository;

import com.gelatoflow.gelatoflow_api.entity.RoleData;
import com.gelatoflow.gelatoflow_api.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleData, Long> {
}
