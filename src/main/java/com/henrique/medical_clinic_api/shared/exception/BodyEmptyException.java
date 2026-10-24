package com.henrique.medical_clinic_api.shared.exception;

import org.springframework.http.HttpStatus;

public class BodyEmptyException extends BusinessException {
    public BodyEmptyException() {
        super("Request with body empty", HttpStatus.BAD_REQUEST);
    }
}
