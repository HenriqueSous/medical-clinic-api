package com.henrique.medical_clinic_api.exception.validation;

import com.henrique.medical_clinic_api.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;

public class AppointmentDurationBelowMinimumException extends BusinessException {
    public AppointmentDurationBelowMinimumException(int duration, int minimumDuration) {
        super("The provided duration "+duration+" is below the required minimum duration "+minimumDuration,
                HttpStatus.BAD_REQUEST);
    }
}
