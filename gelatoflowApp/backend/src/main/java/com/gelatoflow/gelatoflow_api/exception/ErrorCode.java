package com.gelatoflow.gelatoflow_api.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {
    OBJECT_NOT_FOUND("Object {0} with id {1} not found", HttpStatus.NOT_FOUND),
    DEPRECATED_MODIFICATION("The modification date of the new object cannot be older than modification date of the current object", HttpStatus.BAD_REQUEST),
    NULL_ENTITY_FORBIDDEN("Entities cannot be null to be updated", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("Email address is not valid.", HttpStatus.BAD_REQUEST),
    EMAIL_IS_TAKEN("Email address {0} is already taken.", HttpStatus.CONFLICT),
    NO_USERS_FOUND_WITH_CRITERIA("No users found with provided criteria.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User with id {0} not found.", HttpStatus.NOT_FOUND),
    SHOP_NOT_FOUND("Shop with id {0} not found.", HttpStatus.NOT_FOUND),
    SHOP_NAME_IS_TAKEN("The name {0} is already taken.", HttpStatus.CONFLICT),
    FAILED_TO_CREATE_USER("Failed to create user.", HttpStatus.BAD_REQUEST),
    FAILED_TO_UPDATE_USER("Failed to update user.", HttpStatus.BAD_REQUEST),
    FAILED_TO_DELETE_USER("Failed to delete user.", HttpStatus.BAD_REQUEST),
    FAILED_TO_CREATE_SHOP("Failed to create shop.", HttpStatus.BAD_REQUEST),
    FAILED_TO_UPDATE_SHOP("Failed to update shop.", HttpStatus.BAD_REQUEST),
    FAILED_TO_DELETE_SHOP("Failed to delete shop.", HttpStatus.BAD_REQUEST),
    FAILED_TO_ADD_USER_TO_SHOP("Failed to add user to shop.", HttpStatus.BAD_REQUEST),
    FAILED_TO_REMOVE_USER_FROM_SHOP("Failed to remove user from shop.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_SHOP("User is already in this shop.", HttpStatus.BAD_REQUEST),
    FAILED_TO_CHANGE_PASSWORD("Failed to change password.", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("Role not found.", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND("Order with id {0} not found.", HttpStatus.NOT_FOUND),
    FAILED_TO_CREATE_ORDER("Failed to create order.", HttpStatus.BAD_REQUEST ),
    PRODUCT_NOT_FOUND("Product with id {0} not found.", HttpStatus.NOT_FOUND),
    FAILED_TO_CREATE_PRODUCT("Failed to create product.", HttpStatus.BAD_REQUEST ),
    FAILED_TO_UPDATE_PRODUCT("Failed to update product {0}", HttpStatus.BAD_REQUEST ),
    VARIANT_NOT_FOUND("Variant with id {0} not found", HttpStatus.NOT_FOUND ),
    FAILED_TO_DELETE_PRODUCT("Failed to delete product with id {0}",HttpStatus.BAD_REQUEST ),
    PRODUCT_VARIANT_NOT_FOUND("Product variant with id {0} not found.",HttpStatus.NOT_FOUND ),
    ORDER_STATUS_NOT_FOUND("Order status not found", HttpStatus.NOT_FOUND ),
    FAILED_TO_DELETE_ORDER("Failed to delete order with id {0}.", HttpStatus.BAD_REQUEST ),
    INSUFFICIENT_STOCK("Insufficient stock for this product variant",HttpStatus.BAD_REQUEST ),
    ORDER_PRIORITY_NOT_FOUND("Order priority not found.", HttpStatus.NOT_FOUND );

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

}