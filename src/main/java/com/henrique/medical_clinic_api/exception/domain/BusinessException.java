package com.henrique.medical_clinic_api.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
    @Getter
    private HttpStatus httpStatus;

    public BusinessException(String message, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        super(message);
    }
}
