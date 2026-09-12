package com.henrique.medical_clinic_api.exception.resource;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String model, String resourceName, String resource) {
        super(String.format("Duplicate %s with %s '%s'", model, resourceName, resource), HttpStatus.CONFLICT);
    }
}
