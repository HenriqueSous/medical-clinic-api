package com.henrique.medical_clinic_api.exception.resource;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class SpecialtyNotFoundException extends BusinessException {
    public SpecialtyNotFoundException(long id) {
        super("Specialty with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }

    public SpecialtyNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
