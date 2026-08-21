package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends BusinessException {
    public PatientNotFoundException(Long id) {
        super("Patient with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }

    public PatientNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
