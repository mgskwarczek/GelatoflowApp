package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.entity.RoleData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.RoleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@Service
public class RoleService {
    private final Logger logger = LogManager.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    public RoleData getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.ROLE_NOT_FOUND)));
    }

    public List<RoleData> getAllRoles(){
        return roleRepository.findAll();
    }
}
