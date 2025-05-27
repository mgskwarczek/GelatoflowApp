package com.gelatoflow.gelatoflow_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "GF_AUDIT_LOG_VALUES")
@AttributeOverride(name = "creationDate", column = @Column(name = "ALV_CRE_DATE", nullable = false, updatable = false))
public class AuditLogValuesData extends AuditCreationData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALV_ID_SEQ")
    @SequenceGenerator(name = "ALV_ID_SEQ", sequenceName = "ALV_ID_SEQ", initialValue = 50)
    @Column(name = "ALV_ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALV_ALH_ID", referencedColumnName = "ALH_ID")
    private AuditLogHeaderData auditLogHeader;

    @Column(name = "ALV_PREVIOUS_VALUE", updatable = false)
    private String previousValue;

    @Column(name = "ALV_NEW_VALUE", updatable = false)
    private String newValue;

    @Column(name = "ALV_ATTRIBUTE", nullable = false, updatable = false)
    private String attribute;

    public AuditLogValuesData() {}

    public AuditLogValuesData(AuditLogHeaderData auditLogHeaderData,
                              String attribute,
                              String previousValue,
                              String newValue) {
        this.auditLogHeader = auditLogHeaderData;
        this.attribute = attribute;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.setCreationDate(LocalDateTime.now());
    }

}
