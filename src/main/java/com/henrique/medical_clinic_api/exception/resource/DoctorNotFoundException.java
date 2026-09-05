package com.henrique.medical_clinic_api.exception.resource;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends BusinessException {
    public DoctorNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public DoctorNotFoundException(long id) {
        super("Doctor with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }
}
