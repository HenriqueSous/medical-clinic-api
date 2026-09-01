package com.henrique.medical_clinic_api.exception;

import com.henrique.medical_clinic_api.exception.model.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class DoctorScheduleConflictException extends BusinessException {
    public DoctorScheduleConflictException(long doctorId, LocalDateTime appointmentTime) {
        super(String.format("Doctor with ID %d already has an appointment scheduled for %s.", doctorId, appointmentTime),
                HttpStatus.BAD_REQUEST);
    }
}
