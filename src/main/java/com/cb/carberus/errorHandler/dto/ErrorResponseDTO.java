package com.cb.carberus.errorHandler.dto;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String errorCode;

    public ErrorResponseDTO(String errorCode) {
        this.errorCode = errorCode;
    }
}
