package com.cb.carberus.errorHandler.model;

public enum StandardErrorCode implements ErrorCode {
    INVALID_INPUT("INVALID_INPUT", 400),
    UNAUTHORIZED("UNAUTHORIZED", 401);

    private final String code;
    private final int status;

    StandardErrorCode(String code, int status) {
        this.code = code;
        this.status = status;
    }

    @Override
    public String getErrorCode() { return code; }

    @Override
    public int getStatusCode() { return status; }
}
