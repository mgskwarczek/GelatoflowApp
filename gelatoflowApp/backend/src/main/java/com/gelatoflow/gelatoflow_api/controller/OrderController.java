package com.gelatoflow.gelatoflow_api.controller;


import com.gelatoflow.gelatoflow_api.dto.order.*;
import com.gelatoflow.gelatoflow_api.entity.*;
import com.gelatoflow.gelatoflow_api.exception.ApplicationException;
import com.gelatoflow.gelatoflow_api.exception.ErrorCode;
import com.gelatoflow.gelatoflow_api.repository.*;
import com.gelatoflow.gelatoflow_api.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gelatoflow.gelatoflow_api.utils.Helpers.throwAndLogError;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Logger logger = LogManager.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderPriorityRepository orderPriorityRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ICShopRepository icShopRepository;

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable(name="id") Long id) {
        validateId(id);
        OrdersData ordersData = orderService.getById(id);
        return new OrderDto(ordersData);
    }

    public void validateId(Long id) {
        if (id == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "id"));
        }
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderDetails(@PathVariable(name="orderId") Long orderId) {
        return orderService.getOrderDetails(orderId);
    }

    @PostMapping("/admin/create")
    public Long createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        validateOrderCreateDto(orderCreateDto);
        logger.info("OrderCreateDto successfully validated.");

        OrdersData ordersData = new OrdersData();
        ordersData.setTitle(orderCreateDto.getTitle());
        ordersData.setDescription(orderCreateDto.getDescription());
        ordersData.setPriorityId(orderCreateDto.getPriorityId());
        ordersData.setStatusId(orderCreateDto.getStatusId());

        Long orderId = orderService.createOrder(
                ordersData,
                orderCreateDto.getVariants(),
                orderCreateDto.getShopId()
        );

        logger.info("Order successfully created with ID: {}", orderId);
        return orderId;
    }



    private void validateOrderCreateDto(OrderCreateDto orderCreateDto) {
        if (orderCreateDto.getShopId() == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "shopId"));
        }

        if (orderCreateDto.getTitle() == null || orderCreateDto.getTitle().isEmpty()) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "title"));
        }

        if (orderCreateDto.getDescription().length() > 100) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.TOO_LONG_VALUE, "description", orderCreateDto.getDescription().length(), 250));
        }

        if (orderCreateDto.getStatusId() == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "statusId"));
        }

        if (orderCreateDto.getPriorityId() == null) {
            throw throwAndLogError(logger, new ApplicationException(ErrorCode.NULL_VALUE_FORBIDDEN, "priorityId"));
        }
    }


    @PutMapping("/admin/update")
    @Transactional
    public void updateOrder(@Valid @RequestBody OrderCreateDto orderUpdateDto) {
        validateOrderCreateDto(orderUpdateDto);
        logger.info("OrderUpdateDto successfully validated.");

        orderService.updateOrder(orderUpdateDto);

        logger.info("Order with ID {} successfully updated.", orderUpdateDto.getId());
    }


    @DeleteMapping("/delete/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable(name="orderId") Long orderId) {
        orderService.deleteOrder(orderId);
    }



    @GetMapping("/active")
    public List<OrdersData> getActiveOrders() {
        return orderService.getActiveOrders();
    }

    @GetMapping("/status/{statusId}")
    public OrderStatusData getOrderStatus(@PathVariable(name="statusId") Long statusId) {
        return orderService.getOrderStatusById(statusId);
    }
    @PostMapping("/statusCreate")
    public Long createOrderStatus(@RequestBody OrderStatusData orderStatus) {
        return orderService.createOrderStatus(orderStatus);
    }

    @PutMapping("/statusUpdate")
    public void updateOrderStatus(@RequestBody OrderStatusData orderStatus) {
        orderService.updateOrderStatus(orderStatus);
    }


    @GetMapping("/priority/{priorityId}")
    public OrderPriorityData getOrderPriority(@PathVariable(name="priorityId") Long priorityId) {
        return orderService.getOrderPriorityById(priorityId);
    }


    @GetMapping("/variants/stock")
    public List<StockVariantDto> getAllVariantStock() {
        return orderService.getAllStockQuantities();
    }

    @GetMapping("/variants/{variantId}/stock")
    public Long getVariantStock(@PathVariable(name="variantId") Long variantId) {
        return orderService.getStockQuantity(variantId);
    }

    @GetMapping("/list")
    public List<OrderListDto> getAllOrdersForDisplay() {
        logger.info("Fetching all orders for display.");
        return orderService.getAllOrdersForDisplay();
    }

    @GetMapping("/list/filter")
    public List<OrderListDto> getFilteredOrders(
            @RequestParam(name="shopTitle",required = false) String shopTitle,
            @RequestParam(name="shopLocation",required = false) String shopLocation,
            @RequestParam(name="orderTitle",required = false) String orderTitle) {
        logger.info("Fetching filtered orders: shopTitle={}, shopLocation={}, orderTitle={}", shopTitle, shopLocation, orderTitle);
        return orderService.getFilteredOrders(shopTitle, shopLocation, orderTitle);
    }

    @GetMapping("/shop/{shopId}/orders")
    public List<OrderListDto> getOrdersByShop(@PathVariable(name="shopId") Long shopId) {
        ICShopData shop = icShopRepository.findById(shopId)
                .orElseThrow(() -> throwAndLogError(logger, new ApplicationException(ErrorCode.SHOP_NOT_FOUND, shopId)));

        return shop.getOrders().stream()
                .map(order -> new OrderListDto(
                        order.getTitle(),
                        shop.getName(),
                        shop.getLocation(),
                        order.getPriority() != null ? order.getPriority().getPriorityName() : "Unknown",
                        order.getStatus() != null ? order.getStatus().getName() : "Unknown",
                        order.getCreationDate()
                ))
                .toList();
    }

    @PostMapping("/priority/create")
    public Long createOrderPriority(@RequestBody OrderPriorityData priorityData) {
        return orderService.createOrderPriority(priorityData);
    }

}
