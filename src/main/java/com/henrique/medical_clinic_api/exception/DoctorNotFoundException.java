package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends BusinessException {
    public DoctorNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public DoctorNotFoundException(long id) {
        super("Doctor with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }
}
