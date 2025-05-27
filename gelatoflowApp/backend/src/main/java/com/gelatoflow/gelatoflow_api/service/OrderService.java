package com.gelatoflow.gelatoflow_api.service;

import com.gelatoflow.gelatoflow_api.dto.order.*;
import com.gelatoflow.gelatoflow_api.entity.*;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@Service
public class OrderService {

    @Autowired
    private AuditLogHeaderService auditLogHeaderService;

    private final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderPriorityRepository orderPriorityRepository;

    @Autowired
    private ICShopRepository icShopRepository;

    @Transactional
    protected void saveOrder(OrdersData ordersData, ApplicationException exception) {
        try {
            orderRepository.save(ordersData);
        } catch (ApplicationException e) {
            logger.error("Failed to save this change in OrdersData: {}", e.getMessage(), e);
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.FAILED_TO_CREATE_ORDER, e));
        }
    }


    public OrdersData getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.ORDER_NOT_FOUND, id)));
    }

    public List<OrdersData> getAll() {
        return orderRepository.findAll();
    }

    public Set<OrdersData> getByTitle(String title) {
        return orderRepository.findByTitle(title);
    }

    @Transactional
    public Long createOrder(OrdersData ordersData, List<OrderVariantDto> variants, Long shopId) {
        ICShopData shop = icShopRepository.findById(shopId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.SHOP_NOT_FOUND, shopId)));

        ordersData.setShop(shop);
        ordersData.setCreationDate(LocalDateTime.now());

        orderRepository.save(ordersData);

        for (OrderVariantDto variantDto : variants) {
            ProductVariantData productVariant = productVariantRepository.findById(variantDto.getVariantId())
                    .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, variantDto.getVariantId())));

            if (productVariant.getQuantity() < variantDto.getQuantity()) {
                throw throwAndLogError(logger, new ApplicationException(ErrorCode.INSUFFICIENT_STOCK, variantDto.getVariantId()));
            }

            productVariant.setQuantity(productVariant.getQuantity() - variantDto.getQuantity());
            productVariantRepository.save(productVariant);

            OrdersProductsData ordersProduct = new OrdersProductsData();
            ordersProduct.setOrder(ordersData);
            ordersProduct.setProduct(productVariant.getProduct());
            ordersProduct.setProductVariant(productVariant);
            ordersProduct.setQuantity(variantDto.getQuantity());

            entityManager.persist(ordersProduct);
        }

        logger.info("Order {} successfully created with {} variants.", ordersData.getTitle(), variants.size());
        return ordersData.getId();
    }




    @Transactional
    public void updateOrder(OrderCreateDto orderUpdateDto) {
        OrdersData existingOrder = orderRepository.findById(orderUpdateDto.getId())
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(
                        ErrorCode.ORDER_NOT_FOUND, orderUpdateDto.getId())));

        existingOrder.setTitle(orderUpdateDto.getTitle());
        existingOrder.setDescription(orderUpdateDto.getDescription());
        existingOrder.setModificationDate(LocalDateTime.now());

        if (orderUpdateDto.getStatusId() != null) {
            OrderStatusData status = orderStatusRepository.findById(orderUpdateDto.getStatusId())
                    .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(
                            ErrorCode.ORDER_STATUS_NOT_FOUND, orderUpdateDto.getStatusId())));
            existingOrder.setStatus(status);
        }

        if (orderUpdateDto.getPriorityId() != null) {
            OrderPriorityData priority = orderPriorityRepository.findById(orderUpdateDto.getPriorityId())
                    .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(
                            ErrorCode.ORDER_PRIORITY_NOT_FOUND, orderUpdateDto.getPriorityId())));
            existingOrder.setPriority(priority);
        }

        Set<OrdersProductsData> currentVariants = new HashSet<>(existingOrder.getOrderProducts());
        for (OrderVariantDto variantDto : orderUpdateDto.getVariants()) {
            ProductVariantData productVariant = productVariantRepository.findById(variantDto.getVariantId())
                    .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(
                            ErrorCode.PRODUCT_VARIANT_NOT_FOUND, variantDto.getVariantId())));

            OrdersProductsData ordersProduct = currentVariants.stream()
                    .filter(op -> op.getProductVariant().getId().equals(variantDto.getVariantId()))
                    .findFirst()
                    .orElse(null);

            if (ordersProduct != null) {
                long quantityDifference = variantDto.getQuantity() - ordersProduct.getQuantity();
                if (productVariant.getQuantity() < quantityDifference) {
                    throw throwAndLogError(logger, new ApplicationException(
                            ErrorCode.INSUFFICIENT_STOCK, variantDto.getVariantId()));
                }

                productVariant.setQuantity(productVariant.getQuantity() - quantityDifference);
                productVariantRepository.save(productVariant);
                ordersProduct.setQuantity(variantDto.getQuantity());
            } else {
                if (productVariant.getQuantity() < variantDto.getQuantity()) {
                    throw throwAndLogError(logger, new ApplicationException(
                            ErrorCode.INSUFFICIENT_STOCK, variantDto.getVariantId()));
                }

                productVariant.setQuantity(productVariant.getQuantity() - variantDto.getQuantity());
                productVariantRepository.save(productVariant);

                OrdersProductsData newOrderProduct = new OrdersProductsData();
                newOrderProduct.setOrder(existingOrder);
                newOrderProduct.setProduct(productVariant.getProduct());
                newOrderProduct.setProductVariant(productVariant);
                newOrderProduct.setQuantity(variantDto.getQuantity());
                entityManager.persist(newOrderProduct);
            }
        }

        currentVariants.removeIf(op -> orderUpdateDto.getVariants().stream()
                .anyMatch(v -> v.getVariantId().equals(op.getProductVariant().getId())));

        for (OrdersProductsData removedVariant : currentVariants) {
            ProductVariantData productVariant = removedVariant.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity() + removedVariant.getQuantity());
            productVariantRepository.save(productVariant);
            entityManager.remove(removedVariant);
        }
        orderRepository.save(existingOrder);
        logger.info("Order {} successfully updated.", existingOrder.getTitle());
    }


    public void deleteOrder(Long id) {
        OrdersData ordersData = getById(id);

        ordersData.setDeletionDate(LocalDateTime.now());
        saveOrder(ordersData, new ApplicationException(ErrorCode.FAILED_TO_DELETE_ORDER, id));

        logger.info("Order with id {} was successfully deleted.", id);
    }



    public OrderStatusData getOrderStatusById(Long id) {
        return orderStatusRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.OBJECT_NOT_FOUND, "suit status", id)));
    }

    public Long createOrderStatus(OrderStatusData orderStatus) {
        orderStatusRepository.save(orderStatus);
        logger.info("Order status with id {} has been created.", orderStatus.getId());
        return orderStatus.getId();
    }

    public void updateOrderStatus(OrderStatusData orderStatus) {
        orderStatusRepository.save(orderStatus);
        logger.info("Order status with id {} has been updated.", orderStatus.getId());
    }





    @Transactional
    public void addVariantToOrder(Long orderId, Long variantId, Long quantity) {
        OrdersData order = orderRepository.findById(orderId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.ORDER_NOT_FOUND, orderId)));

        ProductVariantData variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, variantId)));

        if (variant.getQuantity() < quantity) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.INSUFFICIENT_STOCK, variantId));
        }

        variant.setQuantity(variant.getQuantity() - quantity);
        productVariantRepository.save(variant);

        OrdersProductsData orderProduct = new OrdersProductsData();
        orderProduct.setOrder(orderRepository.findById(orderId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.ORDER_NOT_FOUND, orderId))));
        orderProduct.setProductVariant(variant);
        orderProduct.setQuantity(quantity);

        entityManager.persist(orderProduct);
        logger.info("Variant ID '{}' with quantity '{}' added to order ID '{}'.", variantId, quantity, orderId);
    }


    public List<StockVariantDto> getAllStockQuantities() {
        return productVariantRepository.findAll().stream()
                .map(variant -> new StockVariantDto(variant.getId(), variant.getQuantity()))
                .toList();
    }


    public Long getStockQuantity(Long variantId) {
        ProductVariantData variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, variantId)));
        return variant.getQuantity();
    }


    public OrderDto getOrderDetails(Long orderId) {
        OrdersData order = getById(orderId);

        List<OrderVariantDto> variants = order.getOrderProducts().stream()
                .map(orderProduct -> {
                    OrderVariantDto dto = new OrderVariantDto();
                    dto.setVariantId(orderProduct.getProductVariant().getId());
                    dto.setQuantity(orderProduct.getQuantity());
                    return dto;
                }).toList();

        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setTitle(order.getTitle());
        orderDto.setDescription(order.getDescription());
        orderDto.setVariants(variants);

        return orderDto;
    }

    public OrderPriorityData getOrderPriorityById(Long id) {
        return orderPriorityRepository.findById(id)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.OBJECT_NOT_FOUND, "order priority", id)));
    }


    public List<OrdersData> getActiveOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getDeletionDate() == null)
                .toList();
    }

    public List<OrderListDto> getAllOrdersForDisplay() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderListDto(
                        order.getTitle(),
                        order.getShop() != null ? order.getShop().getName() : "Unknown",
                        order.getShop() != null ? order.getShop().getLocation() : "Unknown",
                        order.getPriority() != null ? order.getPriority().getPriorityName() : "Unknown",
                        order.getStatus() != null ? order.getStatus().getName() : "Unknown",
                        order.getCreationDate()
                ))
                .toList();
    }

    public List<OrderListDto> getFilteredOrders(String shopTitle, String shopLocation, String orderTitle) {
        return orderRepository.findAll().stream()
                .filter(order -> shopTitle == null || (order.getShop() != null && order.getShop().getName().contains(shopTitle)))
                .filter(order -> shopLocation == null || (order.getShop() != null && order.getShop().getLocation().contains(shopLocation)))
                .filter(order -> orderTitle == null || order.getTitle().contains(orderTitle))
                .map(order -> new OrderListDto(
                        order.getTitle(),
                        order.getShop() != null ? order.getShop().getName() : "Unknown",
                        order.getShop() != null ? order.getShop().getLocation() : "Unknown",
                        order.getPriority() != null ? order.getPriority().getPriorityName() : "Unknown",
                        order.getStatus() != null ? order.getStatus().getName() : "Unknown",
                        order.getCreationDate()
                ))
                .toList();
    }

    @Transactional
    public Long createOrderPriority(OrderPriorityData priorityData) {
        orderPriorityRepository.save(priorityData);
        logger.info("Order priority '{}' created successfully.", priorityData.getPriorityName());
        return priorityData.getId();
    }

}


