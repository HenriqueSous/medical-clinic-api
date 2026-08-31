package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class DuplicateAppointmentDateException extends BusinessException {
    public DuplicateAppointmentDateException(long patientId, LocalDate date) {
        super("Patient with ID '"+patientId+"' already has an appointment scheduled for "+date,
                HttpStatus.BAD_REQUEST);
    }
}
