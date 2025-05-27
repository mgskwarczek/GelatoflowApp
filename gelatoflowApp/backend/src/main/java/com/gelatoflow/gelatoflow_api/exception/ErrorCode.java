package com.gelatoflow.gelatoflow_api.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {
    DEPRECATED_MODIFICATION("The modification date of the new object cannot be older than modification date of the current object", HttpStatus.BAD_REQUEST),
    NULL_ENTITY_FORBIDDEN("Entities cannot be null to be updated", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("Email address is not valid.", HttpStatus.BAD_REQUEST),
    EMAIL_IS_TAKEN("Email address {0} is already taken.", HttpStatus.CONFLICT),
    NO_USERS_FOUND_WITH_CRITERIA("No users found with provided criteria.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User with id {0} not found.", HttpStatus.NOT_FOUND),
    SHOP_NOT_FOUND("Shop with id {0} not found.", HttpStatus.NOT_FOUND),
    FAILED_TO_CREATE_USER("Failed to create user.", HttpStatus.BAD_REQUEST),
    FAILED_TO_UPDATE_USER("Failed to update user.", HttpStatus.BAD_REQUEST),
    FAILED_TO_DELETE_USER("Failed to delete user.", HttpStatus.BAD_REQUEST),
    FAILED_TO_CHANGE_PASSWORD("Failed to change password.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

}