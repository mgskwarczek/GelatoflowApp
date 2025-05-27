package com.gelatoflow.gelatoflow_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "GF_ORDERS")
@NoArgsConstructor
public class OrdersData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK_ORD_ID_SEQ")
    @SequenceGenerator(name = "PK_ORD_ID_SEQ", sequenceName = "PK_ORD_ID_SEQ", initialValue = 50)

    @Column(name = "ORD_ID")
    private Long id;

    @Column(name="ORD_TITLE", length =100, nullable = false)
    private String title;

    @Column(name="ORD_DESCRIPTION", length =250, nullable = false)
    private String description;

    @Column(name="ORD_CRE_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name="ORD_MOD_DATE")
    private LocalDateTime modificationDate;

    @Column(name="ORD_DEL_DATE")
    private LocalDateTime deletionDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORD_STATUS", referencedColumnName = "OST_ID", nullable = false, updatable = false, insertable = false)
    private OrderStatusData status;

    @Column(name = "ORD_STATUS", nullable = false)
    private Long statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORD_PRIORITY", referencedColumnName = "OPR_ID", nullable = false, updatable = false, insertable = false)
    private OrderPriorityData priority;

    @Column(name = "ORD_PRIORITY", nullable = false)
    private Long priorityId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrdersProductsData> orderProducts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_ICS_ID", referencedColumnName = "ICS_ID")
    private ICShopData shop;

    @Column(name = "ORD_ICS_ID", nullable = false, insertable = false, updatable = false)
    private Long shopId;

    public void setShop(ICShopData shop) {
        this.shop = shop;
        this.shopId = (shop != null) ? shop.getId() : null;
    }
    public void setShopId(Long shopId) {
        this.shopId = shopId;

        if (shop != null && !shop.getId().equals(shopId)) {
            this.shop = null;
        }
    }

    public OrdersData(Long id, String title, String description, Long statusId, Long priorityId, Long shopId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.statusId = statusId;
        this.priorityId = priorityId;
        this.shopId = shopId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

}
