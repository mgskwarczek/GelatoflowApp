package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.dto.auditLogValues.LogValueDto;
import com.gelatoflow.gelatoflow_api.entity.AuditFieldsData;
import com.gelatoflow.gelatoflow_api.entity.AuditLogValuesData;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.AuditLogHeaderRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.gelatoflow.gelatoflow_api.repository.UserRepository;
import com.gelatoflow.gelatoflow_api.entity.AuditLogHeaderData;
import java.util.ArrayList;
import java.util.List;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@Service
public class AuditLogHeaderService {
    private static final Logger logger = LogManager.getLogger(AuditLogHeaderService.class);

    private final int MAX_VALUE_LENGTH = 4000;

    @Autowired
    private AuditLogHeaderRepository auditLogHeaderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AuditLogHeaderData> getObjectChanges(Long objectId, String entityName) {
        return auditLogHeaderRepository.findHeadersByRecordPKAndEntityName(objectId, entityName);
    }

    @Transactional
    public <T extends AuditFieldsData<T>> void createObjectChange(T entity, T updatedEntity, Long userId, String entityClass, Long entityId) {
        if (entity == null && updatedEntity == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_ENTITY_FORBIDDEN));
        }
        if (!entity.getClass().isAnnotationPresent(Table.class)) {
            logger.warn("Entity without class annotation: {}", entity.getClass());
            return;
        }

        if (updatedEntity.getModificationDate() != null && entity.getModificationDate() != null && entity.getModificationDate().isAfter(updatedEntity.getModificationDate())) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.DEPRECATED_MODIFICATION));
        }

        AuditLogHeaderData auditLogHeaderData = new AuditLogHeaderData(entityClass, entityId, userId);
        List<LogValueDto> changes = entity.compare(updatedEntity);

        if (changes.isEmpty()) {
            return;
        }

        saveChanges(auditLogHeaderData, changes);
    }

    private void saveChanges(AuditLogHeaderData auditLogHeaderData, List<LogValueDto> changes) {
        List<AuditLogValuesData> values = new ArrayList<>();
        for (LogValueDto change : changes) {
            AuditLogValuesData value = new AuditLogValuesData(
                    auditLogHeaderData,
                    change.attribute(),
                    prepareValue(change.previousValue()),
                    prepareValue(change.newValue()));
            values.add(value);
        }

        auditLogHeaderData.setValues(values);
        auditLogHeaderRepository.save(auditLogHeaderData);
        logger.info("New audit log created, id: {}", auditLogHeaderData.getId());
    }

    public String prepareValue(String value) {
        if (value == null) {
            return null;
        }

        if (value.length() > MAX_VALUE_LENGTH) {
            return value.substring(0, MAX_VALUE_LENGTH - 3) + "...";
        }
        return value;
    }

}

