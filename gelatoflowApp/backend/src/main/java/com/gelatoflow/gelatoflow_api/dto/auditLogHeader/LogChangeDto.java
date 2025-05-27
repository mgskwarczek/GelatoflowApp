package com.gelatoflow.gelatoflow_api.dto.auditLogHeader;

import com.gelatoflow.gelatoflow_api.dto.auditLogValues.LogValueDto;
import com.gelatoflow.gelatoflow_api.dto.user.UserSendDto;
import com.gelatoflow.gelatoflow_api.entity.AuditLogHeaderData;
import com.gelatoflow.gelatoflow_api.entity.UserData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record LogChangeDto(Long objectId,
                           LocalDateTime changeDate,
                           Long changedObjectId,
                           UserSendDto user,
                           List<LogValueDto> changeValues) {

    public static LogChangeDto toDto(AuditLogHeaderData auditLogHeader) {
        UserData user = auditLogHeader.getUser();
        return new LogChangeDto(
                auditLogHeader.getId(),
                auditLogHeader.getCreationDate(),
                auditLogHeader.getRecordPK(),
                UserSendDto.toDto(user),
                auditLogHeader.getValues()
                        .stream()
                        .map(LogValueDto::toDto)
                        .collect(Collectors.toList())
        );
    }
}