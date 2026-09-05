package com.henrique.medical_clinic_api.exception.validation;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class BodyEmptyException extends BusinessException {
    public BodyEmptyException() {
        super("Request with body empty", HttpStatus.BAD_REQUEST);
    }
}
