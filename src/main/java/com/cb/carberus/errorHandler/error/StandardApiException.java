package com.cb.carberus.errorHandler.error;

import com.cb.carberus.errorHandler.model.StandardErrorCode;

public class StandardApiException extends AbstractApiException {
    private final StandardErrorCode errorCode;

    public StandardApiException(StandardErrorCode code) {
        super(code.getErrorCode());
        this.errorCode = code;
    }

    @Override
    public StandardErrorCode getErrorCode() {
        return errorCode;
    }
}
