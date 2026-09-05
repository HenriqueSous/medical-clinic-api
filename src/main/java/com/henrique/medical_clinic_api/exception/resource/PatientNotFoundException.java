package com.henrique.medical_clinic_api.exception.resource;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends BusinessException {
    public PatientNotFoundException(Long id) {
        super("Patient with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }

    public PatientNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
