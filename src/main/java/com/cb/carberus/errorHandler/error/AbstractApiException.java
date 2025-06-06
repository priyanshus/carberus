package com.cb.carberus.errorHandler.error;

import com.cb.carberus.errorHandler.model.ErrorCode;

public abstract class AbstractApiException extends RuntimeException {
    public abstract ErrorCode getErrorCode();

    public AbstractApiException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return getErrorCode().getStatusCode();
    }

}
