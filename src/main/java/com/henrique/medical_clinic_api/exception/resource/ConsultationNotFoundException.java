package com.henrique.medical_clinic_api.exception.resource;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class ConsultationNotFoundException extends BusinessException {
    public ConsultationNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ConsultationNotFoundException(long id) {
        super("Consultation with id '"+id+"' not found", HttpStatus.NOT_FOUND);
    }
}
