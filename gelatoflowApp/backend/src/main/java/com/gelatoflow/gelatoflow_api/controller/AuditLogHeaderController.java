package com.gelatoflow.gelatoflow_api.controller;
import com.gelatoflow.gelatoflow_api.dto.auditLogHeader.LogChangeDto;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.service.AuditLogHeaderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.stream.Collectors;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;


@RestController
@RequestMapping("/api/auditLog")
public class AuditLogHeaderController {
    private static final Logger logger = LogManager.getLogger(AuditLogHeaderController.class);

    private final int MAX_ENTITY_NAME_LENGTH = 50;

    @Autowired
    private AuditLogHeaderService auditLogHeaderService;

    @GetMapping("/getChanges")
    public List<LogChangeDto> getChanges(@RequestParam("id") Long objectId, @RequestParam("entityName") String entityName) {
        validateEntityName(entityName);
        logger.info("Request for changes sent.");

        return auditLogHeaderService.getObjectChanges(objectId, entityName)
                .stream()
                .map(LogChangeDto::toDto)
                .collect(Collectors.toList());
    }

    public void validateEntityName(String entityName) {
        int length = entityName.length();
        if (length > MAX_ENTITY_NAME_LENGTH) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_STRING_FOUND, entityName, length));
        } else if (length == 0) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.EMPTY_VALUE_FORBIDDEN, "Entity name " + entityName));
        }
    }

}
