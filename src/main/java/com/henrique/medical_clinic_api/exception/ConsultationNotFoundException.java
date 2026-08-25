package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

public class ConsultationNotFoundException extends BusinessException {
    public ConsultationNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ConsultationNotFoundException(long id) {
        super("Consultation with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }
}
