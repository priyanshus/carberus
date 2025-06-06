package com.cb.carberus.errorHandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String errorCode;

    public ErrorResponseDTO(String errorCode) {
        this.errorCode = errorCode;
    }
}
