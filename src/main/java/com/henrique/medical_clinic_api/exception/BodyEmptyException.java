package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

public class BodyEmptyException extends BusinessException {
    public BodyEmptyException() {
        super("Request with body empty", HttpStatus.BAD_REQUEST);
    }
}
