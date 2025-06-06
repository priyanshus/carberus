package com.cb.carberus.errorHandler.model;

public enum DomainErrorCode implements ErrorCode {
    // User Service
    EMAIL_ALREADY_TAKEN("EMAIL_ALREADY_TAKEN", 400),
    NO_USERS_FOUND("NO_USERS_FOUND", 400),

    TEST_CYCLE_CLOSED("TEST_CYCLE_CLOSED", 422),
    CANNOT_DELETE_ADMIN("CANNOT_DELETE_ADMIN", 400);

    private final String code;
    private final int status;

    DomainErrorCode(String code, int status) {
        this.code = code;
        this.status = status;
    }

    @Override
    public String getErrorCode() { return code; }

    @Override
    public int getStatusCode() { return status; }
}
