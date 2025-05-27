package com.gelatoflow.gelatoflow_api.dto.auditLogValues;


import com.gelatoflow.gelatoflow_api.entity.AuditLogValuesData;

public record LogValueDto(String attribute,
                          String previousValue,
                          String newValue) {

    public static LogValueDto toDto(AuditLogValuesData value) {
        return new LogValueDto(
                value.getAttribute(),
                value.getPreviousValue(),
                value.getNewValue()
        );
    }

}
