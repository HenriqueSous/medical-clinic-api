package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class PatientAlreadyHasAppointmentOnDateException extends BusinessException {
    public PatientAlreadyHasAppointmentOnDateException(long id, LocalDate localDate) {
        super("The patient with id '"+id+"' already has an appointment scheduled for "+localDate, HttpStatus.BAD_REQUEST);
    }
}
