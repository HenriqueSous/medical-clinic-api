package com.henrique.medical_clinic_api.exception.validation;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class ImmutableFieldException extends BusinessException {
    public ImmutableFieldException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
