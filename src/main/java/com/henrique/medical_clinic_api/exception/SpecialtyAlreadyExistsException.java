package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

public class SpecialtyAlreadyExistsException extends BusinessException {
    public SpecialtyAlreadyExistsException(String name) {
        super(String.format("A specialty named '%s' already exists.", name), HttpStatus.BAD_REQUEST);
    }
}
