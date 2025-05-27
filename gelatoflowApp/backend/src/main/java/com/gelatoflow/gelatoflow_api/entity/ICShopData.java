package com.gelatoflow.gelatoflow_api.entity;

import com.gelatoflow.gelatoflow_api.dto.auditLogValues.LogValueDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "GF_IC_SHOPS")
@NoArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "creationDate", column = @Column(name = "ICS_CRE_DATE", nullable = false, updatable = false)),
        @AttributeOverride(name = "modificationDate", column = @Column(name = "ICS_MOD_DATE")),
        @AttributeOverride(name = "deletionDate", column = @Column(name = "ICS_DEL_DATE"))
})
public class ICShopData extends AuditFieldsData<ICShopData>{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_ICS_ID_SEQ")
    @SequenceGenerator(name = "PK_ICS_ID_SEQ", sequenceName = "PK_ICS_ID_SEQ", initialValue = 50)
    @Column(name = "ICS_ID")
    private Long id;

    @Column(name = "ICS_NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "ICS_LOCATION", nullable = false, length = 100)
    private String location;

    @ManyToOne
    @JoinColumn(name = "ICS_OWNER_ID", referencedColumnName = "USR_ID", nullable = false, insertable = false, updatable = false)
    private UserData owner;

    @Column(name="ICS_OWNER_ID",nullable = false)
    private Long ownerId;

    @ManyToMany
    @JoinTable(
            name = "GF_USERS_SHOPS",
            joinColumns = @JoinColumn(name = "USH_ICS_ID"),
            inverseJoinColumns = @JoinColumn(name = "USH_USR_ID")
    )
    private Set<UserData> users = new HashSet<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private Set<OrdersData> orders;

    public ICShopData(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ICShopData(Long id, String name, String location, Long ownerId) {
        this.id = id;
        this.name = name;
        this.location= location;
        this.ownerId = ownerId;
    }


    @Override
    public List<LogValueDto> compare(ICShopData entity) {
        List<LogValueDto> changes = new ArrayList<>();

        if (entity.getName() != null && !name.equals(entity.getName())) {
            changes.add(new LogValueDto("name", name, entity.getName()));
        }
        return changes;
    }
}
