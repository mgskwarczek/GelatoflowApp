package com.gelatoflow.gelatoflow_api.entity;


import com.gelatoflow.gelatoflow_api.dto.auditLogValues.LogValueDto;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditFieldsData<T> extends AuditCreationData {
    @Column(name=" ")
    private LocalDateTime modificationDate;

    @Column(name=" ")
    private LocalDateTime deletionDate;

    @Transactional
    public abstract List<LogValueDto> compare(T entity);
}
