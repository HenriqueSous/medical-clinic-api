package com.henrique.medical_clinic_api.features.consultation;

import com.henrique.medical_clinic_api.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PendingConsultationsException extends BusinessException {
    public PendingConsultationsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
