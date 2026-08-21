package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

public class SpecialtyNotFoundException extends BusinessException {
    public SpecialtyNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
