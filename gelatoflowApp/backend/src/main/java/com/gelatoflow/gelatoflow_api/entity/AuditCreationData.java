package com.gelatoflow.gelatoflow_api.entity;

import com.gelatoflow.gelatoflow_api.utils.EntityIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditCreationData implements EntityIdentifiable {

    @Column(name = " ", nullable = false, updatable = false)
    private LocalDateTime creationDate;

}
