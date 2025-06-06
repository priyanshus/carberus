package com.cb.carberus.errorHandler.error;

import com.cb.carberus.errorHandler.model.DomainErrorCode;

public class DomainException extends AbstractApiException {
    private final DomainErrorCode errorCode;

    public DomainException(DomainErrorCode code) {
        super(code.getErrorCode());
        this.errorCode = code;
    }

    @Override
    public DomainErrorCode getErrorCode() {
        return errorCode;
    }
}
