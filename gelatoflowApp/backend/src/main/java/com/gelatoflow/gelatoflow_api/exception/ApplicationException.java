package com.gelatoflow.gelatoflow_api.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] details;
    private final String parametrisedMessage;

    public ApplicationException(ErrorCode errorCode, Object... details) {
        super(errorCode.getMessage());

        this.errorCode = errorCode;
        this.details = details;
        this.parametrisedMessage = formatMessage(errorCode.getMessage(), details);
    }

    private String formatMessage(String message, Object... details) {
        String formattedMessage = message;

        for (int i = 0; i < details.length; i++) {
            formattedMessage = formattedMessage.replace("{" + i + "}", details[i].toString());
        }

        return formattedMessage;
    }
}
