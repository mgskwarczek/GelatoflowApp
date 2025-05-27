package com.gelatoflow.gelatoflow_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "GF_AUDIT_LOG_HEADER")
@AttributeOverride(name = "creationDate", column = @Column(name = "ALH_CHANGE_DATE", nullable = false, updatable = false))
public class AuditLogHeaderData extends AuditCreationData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALH_ID_SEQ")
    @SequenceGenerator(name = "ALH_ID_SEQ", sequenceName = "ALH_ID_SEQ", initialValue = 50)
    @Column(name = "ALH_ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ALH_ENTITY_NAME", nullable = false, updatable = false)
    private String entityName;

    @Column(name = "ALH_RECORD_PK", nullable = false, updatable = false)
    private Long recordPK;

    @OneToMany(mappedBy = "auditLogHeader", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AuditLogValuesData> values;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALH_USR_ID", referencedColumnName = "USR_ID", updatable = false, insertable = false)
    private UserData user;

    @Column(name = "ALH_USR_ID", nullable = false)
    private Long userId;

    public AuditLogHeaderData() {}

    public AuditLogHeaderData(String entityName, Long recordPK, Long user) {
        this.entityName = entityName;
        this.recordPK = recordPK;
        this.userId = user;
        this.setCreationDate(LocalDateTime.now());
    }

}
