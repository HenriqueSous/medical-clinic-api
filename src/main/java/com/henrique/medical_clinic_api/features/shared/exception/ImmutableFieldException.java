package com.henrique.medical_clinic_api.features.shared.exception;

import org.springframework.http.HttpStatus;

public class ImmutableFieldException extends BusinessException {
    public ImmutableFieldException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
